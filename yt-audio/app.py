"""YouTube -> high-quality audio converter.

Downloads the best available audio stream from a YouTube link with yt-dlp
and converts it with ffmpeg into the format the user picks.
"""
import mimetypes
import os
import re
import shutil
import socket
import sys
import tempfile
from urllib.parse import quote, urlparse

from flask import Flask, Response, jsonify, render_template, request
import yt_dlp

app = Flask(__name__)

ALLOWED_HOSTS = {
    "youtube.com", "www.youtube.com", "m.youtube.com", "music.youtube.com", "youtu.be",
}

# format -> (ffmpeg codec for yt-dlp, quality). Quality is a bitrate in kbps for
# lossy codecs; lossless formats ignore it.
FORMATS = {
    "mp3": ("mp3", "320"),
    "m4a": ("m4a", "256"),
    "opus": ("opus", "192"),
    "flac": ("flac", "0"),
    "wav": ("wav", "0"),
    "original": (None, None),  # best source stream, no re-encode
}

MAX_DURATION_SECONDS = int(os.environ.get("MAX_DURATION_SECONDS", 3 * 60 * 60))


def is_youtube_url(url):
    try:
        parsed = urlparse(url)
    except ValueError:
        return False
    return parsed.scheme in ("http", "https") and (parsed.hostname or "").lower() in ALLOWED_HOSTS


def safe_filename(name):
    name = re.sub(r'[\\/:*?"<>|\x00-\x1f]', "", name).strip()
    return name[:150] or "audio"


def build_options(workdir, fmt):
    codec, quality = FORMATS[fmt]
    opts = {
        "format": "bestaudio/best",
        "outtmpl": os.path.join(workdir, "%(id)s.%(ext)s"),
        "noplaylist": True,
        "quiet": True,
        "no_warnings": True,
        "noprogress": True,
        "match_filter": yt_dlp.utils.match_filter_func(f"duration <=? {MAX_DURATION_SECONDS}"),
    }
    if codec:
        opts["postprocessors"] = [{
            "key": "FFmpegExtractAudio",
            "preferredcodec": codec,
            "preferredquality": quality,
        }]
    return opts


def download_audio(url, fmt, workdir):
    """Download and convert; returns (path, title)."""
    with yt_dlp.YoutubeDL(build_options(workdir, fmt)) as ydl:
        info = ydl.extract_info(url, download=True)
    if not info or info.get("_type") == "playlist":
        raise ValueError("Could not read that video.")
    files = [f for f in os.listdir(workdir) if not f.endswith((".part", ".ytdl"))]
    if not files:
        raise ValueError(f"Video is longer than the {MAX_DURATION_SECONDS // 60} minute limit or unavailable.")
    path = os.path.join(workdir, files[0])
    return path, info.get("title") or info.get("id") or "audio"


@app.get("/")
def index():
    return render_template("index.html", formats=list(FORMATS))


@app.post("/api/convert")
def convert():
    data = request.get_json(silent=True) or request.form
    url = (data.get("url") or "").strip()
    fmt = (data.get("format") or "mp3").lower()

    if not is_youtube_url(url):
        return jsonify(error="Please enter a valid YouTube link."), 400
    if fmt not in FORMATS:
        return jsonify(error=f"Unsupported format. Choose one of: {', '.join(FORMATS)}."), 400

    workdir = tempfile.mkdtemp(prefix="yt-audio-")
    try:
        path, title = download_audio(url, fmt, workdir)
    except yt_dlp.utils.DownloadError as e:
        shutil.rmtree(workdir, ignore_errors=True)
        msg = re.sub(r"^ERROR:\s*", "", str(e))
        return jsonify(error=f"Download failed: {msg}"), 422
    except ValueError as e:
        shutil.rmtree(workdir, ignore_errors=True)
        return jsonify(error=str(e)), 422
    except Exception:
        shutil.rmtree(workdir, ignore_errors=True)
        raise

    def stream():
        # Stream the file, then remove the temp dir once the client has it.
        try:
            with open(path, "rb") as f:
                while chunk := f.read(64 * 1024):
                    yield chunk
        finally:
            shutil.rmtree(workdir, ignore_errors=True)

    ext = os.path.splitext(path)[1]
    filename = safe_filename(title) + ext
    headers = {
        "Content-Disposition": f"attachment; filename*=UTF-8''{quote(filename)}",
        "Content-Length": str(os.path.getsize(path)),
    }
    return Response(stream(), mimetype=mimetypes.guess_type(filename)[0] or "application/octet-stream",
                    headers=headers)


def lan_ip():
    # Connecting a UDP socket sends nothing; it just picks the outward-facing interface.
    with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as s:
        try:
            s.connect(("10.255.255.255", 1))
            return s.getsockname()[0]
        except OSError:
            return "127.0.0.1"


if __name__ == "__main__":
    if not shutil.which("ffmpeg"):
        raise SystemExit("ffmpeg is required: install it and make sure it is on your PATH.")
    port = int(os.environ.get("PORT", 5000))
    # --lan makes the app reachable from other devices (e.g. your phone) on the same Wi-Fi.
    host = "0.0.0.0" if "--lan" in sys.argv else os.environ.get("HOST", "127.0.0.1")
    if host == "0.0.0.0":
        print(f"\n  Open this on your phone (same Wi-Fi): http://{lan_ip()}:{port}\n")
    app.run(host=host, port=port, debug=False)
