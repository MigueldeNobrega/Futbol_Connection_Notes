package com.example.futbol_connection_notes;

public class Message {

        private String usuario;
        private String mensaje;

        // Constructor vacío requerido por Firebase
        public Message() {}

        public Message(String usuario, String mensaje) {
            this.usuario = usuario;
            this.mensaje = mensaje;
        }


        public String getUsuario() {
            return usuario;
        }

        public String getMensaje() {
            return mensaje;
        }
    }



