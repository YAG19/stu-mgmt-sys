package com.student.mgmtsys.repository;

import com.student.mgmtsys.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    @Query("SELECT s FROM Student s WHERE s.name = :name")
    List<Student> findByName(String name);

    Optional<Student> findByCode(String code);


}
