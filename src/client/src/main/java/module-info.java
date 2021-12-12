module tetris.client {
  requires tetris.model;
  requires tetris.message;
  requires javafx.base;
  requires javafx.graphics;
  requires javafx.controls;
  requires javafx.fxml;
  requires java.desktop;
  requires java.logging;

  exports esi.acgt.atlj.client;
  exports esi.acgt.atlj.client.model;
  exports esi.acgt.atlj.client.view;
  exports esi.acgt.atlj.client.view.components;
  exports esi.acgt.atlj.client.controller;
  exports esi.acgt.atlj.client.view.controllers;
  exports esi.acgt.atlj.client.model.game;
}