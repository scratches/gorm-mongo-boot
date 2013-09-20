package demo

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional

@Component
class Starter implements CommandLineRunner {

  @Transactional
  void run(String... args) {
    Book.list().each { it.delete() }
    new Book(title: "Foo", author: "Bar", pages: 123).save()
    println Book.list()
  }

}
