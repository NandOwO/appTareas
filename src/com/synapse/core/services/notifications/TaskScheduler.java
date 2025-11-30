package com.synapse.core.services.notifications;

import com.synapse.core.models.Tarea;
import com.synapse.data.dao.TareaDAO;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author FERNANDO
 */
public class TaskScheduler implements Runnable {
    
    private final List<TaskObserver> observers = new ArrayList<>();
    private final TareaDAO tareaDAO; 
    private volatile boolean running = true; 
    private final long CHECK_INTERVAL_MS = 3600 * 1000;
    
    public TaskScheduler(){
        this.tareaDAO = new TareaDAO();
    }
    
    public void addObserver(TaskObserver observer){
        observers.add(observer);
    }
    
    public void removeObserver(TaskObserver observer){
        observers.remove(observer);
    }
    
    public void notifyObservers(List<Tarea> tareas){
        for (TaskObserver observer : observers) {
            observer.onTaskDue(tareas);
        }
    }
    
    public void stopScheduler(){
        this.running = false; 
    }
    
    @Override
    public void run() {
        System.out.println("TaskScheduler iniciado. Revisando tareas cada hora");
        while (running) {
            try {
                List<Tarea> tareasProximas = tareaDAO.buscarTareasProximasAVencer(1);
                if (!tareasProximas.isEmpty()) {
                    System.out.println("Se econtraron " + tareasProximas.size() + " tareas proximas a vencer.");
                    notifyObservers(tareasProximas);
                }
                
                Thread.sleep(CHECK_INTERVAL_MS);
                
            } catch (InterruptedException e) {
                System.out.println("TaskScheduler interrumpido. Deteniendo ...");
                running = false;  
            } catch(Exception e){
                System.out.println("Error en TaskScheduler: " + e.getMessage());
                e.printStackTrace();
            }
            
        }
    }
    
}
