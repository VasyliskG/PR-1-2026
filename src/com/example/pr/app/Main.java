package com.example.pr.app;

import com.example.pr.infrastructure.config.EmailConfig;
import com.example.pr.presentation.Application;

/**
 * Точка входу в застосунок.
 */
public class Main {

  public static void main(String[] args) {
    Application app = new Application();
    app.run();
  }
}
