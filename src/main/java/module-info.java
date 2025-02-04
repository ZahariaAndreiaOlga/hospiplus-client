module org.hospi.hospiplusclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.net.http;
    requires com.google.gson;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires jjwt.api;
    requires org.json;

    opens org.hospi.hospiplusclient to javafx.fxml;
    exports org.hospi.hospiplusclient;
    exports org.hospi.hospiplusclient.controllers;
    opens org.hospi.hospiplusclient.controllers to javafx.fxml;

    opens org.hospi.hospiplusclient.models to com.google.gson;
    exports org.hospi.hospiplusclient.models to com.fasterxml.jackson.databind;

}