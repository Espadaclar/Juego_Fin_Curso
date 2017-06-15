import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.paint.*;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.util.Duration;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

import javafx.scene.control.Button;
import javafx.animation.Animation.Status;
import java.util.Random;
import javafx.scene.shape.Rectangle;

import javafx.scene.input.KeyCode;
import javafx.scene.control.Label;

//PARA CREAR EL CRONÓMETRO.
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Toolkit;

import java.util.ArrayList;
import javafx.scene.shape.Shape;

import java.lang.Object;
import javafx.scene.Node;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Polygon;

/**
 * Write a description of class Circulo here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Aplicacion_0 extends Application 
{
    private Circle cicle;
    private Rectangle rectangulo;
    //velocidad del círculo.
    private int velocidadX = 1;
    private int velocidadY = 1;
    //velocidad de la barra.
    private int velocidadEnBarraX;
    private int velocidadEnBarraY;
    private  Pelota pelota;

    Color COLOR_ESCENA = Color.WHITE;
    private boolean sonidoDeCapturado = false;
    private boolean seCreaUnArbol = false;
    private int cuentaArboles = 0;

    int LARGO_ESCENA = 800;
    int ALTO_ESCENA = 650;

    int LARGO_CAZADOR = 60;
    int ALTO_CAZADOR = 60;
    int POSICION_X_CAZADOR = LARGO_ESCENA /4;
    int POSICION_Y_CAZADOR = ALTO_ESCENA /5;

    int NUM_DE_BOLAS = 11;
    int NUM_DE_ARBOLES = 22;

    int RADIO = 15;

    int LARGO_BOTON = 80;
    int ALTO_BOTON = 5;

    private int contDeTiempo = 1;
    //----- EL CRONOMETRO DEL JUEGO ES DESCENDENTE, EMPIEZA EN 'tiempoEnSegundos'.
    private int tiempoEnSegundos = 966;
    private int numeroBolaEnescena = 0;
    private int eliminados = 0;

    public static void main(String[] args){

        //Esto se utiliza para ejecutar la aplicación 
        //es como el new Contructor()
        launch(args);
    }

    public void start(Stage ventana){//parámetro que va ha ser la ventan de la aplicación

        Group root = new Group(); //contenedor que colocamos dentro de la escena.

        Scene escena = new Scene(root, LARGO_ESCENA, ALTO_ESCENA, COLOR_ESCENA);//Se crea la escena con el contenedor que contiene los objetos.
        ventana.setScene(escena);//pasamos al parámetro ventana el objeto escena.

        //SE CREA EL POLIGONO CAZADOR DE BOLITAS.
        Poligono cazador = new Poligono(POSICION_X_CAZADOR, POSICION_Y_CAZADOR, LARGO_CAZADOR, ALTO_CAZADOR, COLOR_ESCENA);
        root.getChildren().add(cazador);

        //////////////////////////////////////////////////SE CREA UNA COLECCioN DE POLIGONOS ARBOL.
        ArrayList<Poligono> arboles = new ArrayList<>();
        int coorDeX = LARGO_ESCENA /9 ;//------variable para determinar la coordenada de X.
        int acumCoorDeX = 0; 
        for(int i = 0; i < NUM_DE_ARBOLES ; i ++){
            int coorDeY = (ALTO_ESCENA - 150);
            if(i >= 18){
                coorDeY = ALTO_ESCENA - 450;
                acumCoorDeX = 0;
            }
            else if(i >= 9 && i < 18){
                coorDeY = ALTO_ESCENA - 300;
                acumCoorDeX = 0;
            }
            
            Poligono arbol = new Poligono(acumCoorDeX, coorDeY, 10, 100, Color.BLACK);
            acumCoorDeX += coorDeX;//----------acumula la medida de las coordenadas en X.
            arbol.setVisible(false);
            root.getChildren().add(arbol);
            arboles.add(arbol);
        }

        ///////////////////////////////////////////////// SE CREA UNA COLECCioN DE PELOTAS.
        ArrayList<Pelota> pelotas = new ArrayList<>();
        pelota =  new  Pelota( LARGO_ESCENA/2,ALTO_ESCENA/2, RADIO);
        for(int i = 0; i < NUM_DE_BOLAS ; i ++){
            Random ale = new Random();
            pelota = new Pelota(ale.nextInt(LARGO_ESCENA/2), ale.nextInt(ALTO_ESCENA/2), 10);
            //pelota = new Pelota(ale.nextInt(LARGO_ESCENA/2), ale.nextInt(ALTO_ESCENA/2), ale.nextInt(RADIO) +10);
            pelotas.add(pelota);
        }
        for(int i = 0; i < NUM_DE_BOLAS; i ++ ){// añade pelotas a la escena.
            root.getChildren().add(pelotas.get(i));
        }

        //////////////////////////////////////SE CREA UN  CRONÓMETRO

        Label tiempoPasado = new Label("0");  //-------------objeto Label para pasar como parametro.
        Objetos_De_Apollo miLabelCronometro = new Objetos_De_Apollo();//-Objetos_De_Apollo, hecha en este proyecto.
        miLabelCronometro.crearUnLabel(tiempoPasado, root,12, 25);//-------muestra cronometro  de descuento.

        //SE CREA EL  CONTADOR DEL Nº DE  BOLITAS QUE SE VAN ELIMINANDO,       
        Label bolitasEliminadas = new Label();  //-------------objeto Label para pasar como parametro.
        Objetos_De_Apollo miLabelContador = new Objetos_De_Apollo();//-Objetos_De_Apollo, hecha en este proyecto.
        miLabelContador.crearUnLabel(bolitasEliminadas, root, 12, 60);//-------muestra el nº de bolitas eliminadas.

        ////////////////////////////////////////// SE CREA UN BOTÓN
        Button boton = new Button("Stop / Move");
        boton.setDefaultButton(true);
        boton.setLayoutX(15);
        boton.setLayoutY(ALTO_ESCENA - (ALTO_BOTON +10 ));
        boton.setPrefSize(LARGO_BOTON, ALTO_BOTON);
        root.getChildren().add(boton);

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.setAutoReverse(true);
        //define un valor de movimiento en los ejes x / y.
        KeyFrame kf = new KeyFrame(Duration.seconds(.004), new EventHandler<ActionEvent>() {
                    ////// ---------VARIABLE PARA PERMITIR EL RECUENTO DE LAS BARRITAS QUE SE VAN ELIMINADO.
                    int val = root.getChildren().size();
                    boolean sonidoDeCapturados = false;
                    public void handle(ActionEvent event) {
                        //DAR MOVIMIENTO A LAS PELOTAS .
                        for(int i = 0; i < NUM_DE_BOLAS; i ++){
                            pelotas.get(i).mover(LARGO_ESCENA, ALTO_ESCENA);                           
                        }

                        eliminados = ( val - (root.getChildren().size()) );///---- nº de bolitas eliminadas.
                        bolitasEliminadas.setText("Eliminadas  " +eliminados);

                        // cada vez que se elimina una bolita aparece un arbol.
                        if(eliminados < 18){
                            arboles.get(eliminados).setVisible(true);
                            arboles.get(0).setFill(COLOR_ESCENA);
                            arboles.get(0).setStroke(COLOR_ESCENA);
                        }

                        // Actualizamos la etiqueta del tiempo
                        int minutos = tiempoEnSegundos / 60;
                        int segundos = tiempoEnSegundos % 60;
                        tiempoPasado.setText(minutos + ":" + segundos);  
                        if(tiempoEnSegundos == 0){
                            timeline.stop();
                            root.getChildren().removeAll(pelotas);
                            Label finDelJuego = new Label("0");  //-------------objeto Label para pasar como parametro.
                            Objetos_De_Apollo miLabelCronometro = new Objetos_De_Apollo();//-Objetos_De_Apollo, hecha en este proyecto.
                            miLabelCronometro.crearUnLabel(finDelJuego, root,(LARGO_ESCENA /2) -100, ALTO_ESCENA /2);//-------muestra cronometro  de descuento.
                            finDelJuego.setText(" -- GANE  OVER -- ");
                        }
                        //PARA QUE SE MUEVA EL CUADRADO CAZADOR .
                        cazador.mover(LARGO_ESCENA, ALTO_ESCENA);

                        for(Pelota pelota: pelotas){
                            if( cazador.capturadaPelota(pelota) == true ){
                                sonidoDeCapturado = true;
                            }
                            else if(cazador.capturadaPelota(pelota) == false){
                                sonidoDeCapturado = false;
                            }
                        }

                    }
                });

        timeline.getKeyFrames().add(kf);
        timeline.play();
        ventana.show();
        //////////////////////  PARA ACTIVAR Y DESACTIVAR EL BOTÓN CUANDO ESTE ACTIVADO.
        boton.setOnAction(event2 -> {
                if (timeline.getStatus() == Status.PAUSED){
                    timeline.play();
                }
                else{
                    timeline.pause();
                } 
            });

        ////////////////////  para controlar AL CAZADOR con los botones de izquierda/derecha.
        root.setOnKeyPressed(event2 ->{
                if(event2.getCode() == KeyCode.RIGHT){
                    cazador.cambiarDireccionDerecha(LARGO_ESCENA);
                }
                else if(event2.getCode() == KeyCode.LEFT){
                    cazador.cambiarDireccionIzquierda();
                }

                else if(event2.getCode() == KeyCode.UP){
                    cazador.cambiarDireccionArriba(ALTO_ESCENA);
                }
                else if(event2.getCode() == KeyCode.DOWN){
                    cazador.cambiarDireccionAbajo();
                }
                else if(event2.getCode() == KeyCode.ENTER){

                    for(Pelota pelota: pelotas){
                        if( cazador.capturadaPelota(pelota) == true ){
                            root.getChildren().remove(pelota);

                        }
                    }

                }

            });
        //---------- REALIZA UNA ACCION CADA UN Nº DETERMINADO DE MILISEGUNDOS.
        TimerTask tarea = new TimerTask() {
                @Override
                public void run() {
                    tiempoEnSegundos--;
                    contDeTiempo ++;
                }                        
            };

        Timer timer = new Timer();
        timer.schedule(tarea, 0, 1000);

        //-----PARA QUE SUENE LA ALARMA CUANDO UNA PELOTA ES ATRAPADA POR EL CAZADOR.

        TimerTask tareaSonido = new TimerTask() {
                @Override
                public void run() {

                    seCreaUnArbol = true;

                }                        
            };

        Timer timerSonido = new Timer();
        timer.schedule(tareaSonido, 0, 5000);
    }

}