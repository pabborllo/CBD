package com.example.cbd;

import java.util.Calendar;

public class Birra{

    private String id;
    private String nombre;
    private Double formato;
    private Double alcohol;
    private String comentario;
    private String fecha;
    private String foto;

    public Birra(){}

    public Birra(String id, String nombre, Double formato, Double alcohol, String comentario){

        Calendar fechaHoy = Calendar.getInstance();
        this.id = id;
        this.nombre = nombre;
        this.formato = formato;
        this.alcohol = alcohol;
        this.comentario = comentario;
        this.fecha = fechaHoy.get((Calendar.DAY_OF_MONTH))+"/"+
                (fechaHoy.get((Calendar.MONTH))+1)+"/"+
                fechaHoy.get((Calendar.YEAR));
        this.foto = "";
    }

    public String getId(){
        return id;
    }

    public String getNombre(){
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getFormato(){
        return formato;
    }

    public void setFormato(Double formato) {
        this.formato = formato;
    }

    public Double getAlcohol(){
        return alcohol;
    }

    public void setAlcohol(Double alcohol) {
        this.alcohol = alcohol;
    }

    public String getComentario(){
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getFecha(){
        return fecha;
    }

    public String getFoto(){
        return foto;
    }

    public void setFoto(String foto){
        this.foto = foto;
    }
}
