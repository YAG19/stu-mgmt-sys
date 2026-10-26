# YouTube → Audio

A small web app that takes a YouTube link and gives you back a high-quality audio file.
It uses [yt-dlp](https://github.com/yt-dlp/yt-dlp) to fetch the best available audio stream
and [ffmpeg](https://ffmpeg.org/) to convert it.

## Formats

| Format     | Settings                         |
|------------|----------------------------------|
| MP3        | 320 kbps                         |
| M4A (AAC)  | 256 kbps                         |
| Opus       | 192 kbps                         |
| FLAC       | lossless                         |
| WAV        | uncompressed PCM                 |
| Original   | best source stream, no re-encode |

YouTube serves lossy audio (typically Opus ~160 kbps or AAC 128 kbps), so no format can add
detail that isn't in the source. **Original** keeps the stream bit-for-bit; FLAC/WAV preserve
it without further loss; MP3/M4A/Opus re-encode at high bitrates for compatibility.

## Run it

Requires Python 3.9+ and ffmpeg on your `PATH`.

```bash
cd yt-audio
python3 -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
python app.py
```

Open http://127.0.0.1:5000, paste a link, pick a format, and click **Convert & download**.

### API

```bash
curl -X POST http://127.0.0.1:5000/api/convert \
  -H 'Content-Type: application/json' \
  -d '{"url":"https://www.youtube.com/watch?v=VIDEO_ID","format":"mp3"}' \
  -OJ
```

### Configuration

| Env var                | Default | Meaning                         |
|------------------------|---------|---------------------------------|
| `PORT`                 | `5000`  | Port to listen on               |
| `MAX_DURATION_SECONDS` | `10800` | Reject videos longer than this  |

If downloads start failing, update yt-dlp (`pip install -U yt-dlp`) — YouTube changes often.

Only download content you have the right to, and respect YouTube's Terms of Service.
