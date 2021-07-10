package com.project.vinance.client.impl.utils;

@FunctionalInterface
public interface Handler<T> {

  void handle(T t);
}
