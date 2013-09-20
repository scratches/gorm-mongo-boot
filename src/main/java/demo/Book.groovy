package demo

import grails.persistence.Entity

@Entity
class Book {
  String title
  String author
  int pages
  String toString() {
    "'${title}' by ${author} (${pages} pages)"
  }
}