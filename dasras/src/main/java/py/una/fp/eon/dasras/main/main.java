package py.una.fp.eon.dasras.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jgrapht.graph.DefaultWeightedEdge;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import py.una.fp.eon.core.GroupResult;
import py.una.fp.eon.core.Grupo;
import py.una.fp.eon.core.Network;
import py.una.fp.eon.core.Secuencia;
import py.una.fp.eon.core.SubtreeGroupResult;
import py.una.fp.eon.core.SubtreeSequenceResult;
import py.una.fp.eon.core.SummaryTotal;
import py.una.fp.eon.dasras.exceptions.FileMapperException;
import py.una.fp.eon.dasras.precalculate.KShortestPath;
import py.una.fp.eon.trafico.GenerarTrafico;
import py.una.fp.eon.trafico.SequenceTraffic;
import py.una.fp.eon.trafico.property.ApplicationParams;
import py.una.fp.eon.core.GroupResult;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Random;
import java.util.Set;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import py.una.fp.eon.core.FrecuencySlot;
import py.una.fp.eon.core.FrecuencySlotStatus;
import py.una.fp.eon.core.Grupo;
import py.una.fp.eon.core.Network;
import py.una.fp.eon.core.Secuencia;
import py.una.fp.eon.core.SequenceResult;
import py.una.fp.eon.core.Solicitud;
import py.una.fp.eon.core.Subtree;
import py.una.fp.eon.core.SubtreeSequence;
import py.una.fp.eon.dasras.algorithm.AlgorithmBase;
import py.una.fp.eon.dasras.precalculate.KShortestPath;
import py.una.fp.eon.trafico.GenerarTrafico;
import py.una.fp.eon.trafico.SequenceTraffic;
import py.una.fp.eon.trafico.property.ApplicationParams;
import py.una.fp.eon.core.Summary;
import py.una.fp.eon.core.SummaryGeneral;
import py.una.fp.eon.core.SummarySequence;


// todo
// ESTANDARIZAR LOS PARAMETROS
// CONTROLAR CON EL PAPER LA SELECCION, CRUCE Y MUTACION

// notes
// algoritmo elitista

public class main {
    
	public static void main(String[] args) throws FileMapperException {
            // Principal
            ApplicationParams params = new ApplicationParams();
            params.init();
            
            // ALGORITMOS HEURISTICOS Y GENETICOS
            // LRG,SALRG,GALRG,GASALRG
            List<String> algoritmos = new ArrayList<String>(Arrays.asList(params.getText("trafico.resolve.algoritmo").split(",")));   
              
            // TRAFICO
            // networkN6S9,networkNSFNET,networkUSNET
            List<String> redes = new ArrayList<String>(Arrays.asList(params.getText("trafico.filename.network").split(",")));
            // Path de donde se encuentran los archivos de la red
            String filePath = params.getText("trafico.path.network");
            // Valor K para el calculo del shortest path
            Integer k = Integer.parseInt(params.getText("trafico.shortestpath.k"));   
            // Cantidad de trafico a generar. Un grupo tiene los request y su shuffle.
            // Total Multicast Request
            Integer cantidadGrupo = Integer.parseInt(params.getText("trafico.group.number"));
            // Cantida de secuencias a ser generadas 
            Integer cantidadDeSecuencias = Integer.parseInt(params.getText("trafico.request.secuencia.times.max"));
            // 5,10,15,30,50
            List<String> requestsNumber = new ArrayList<String>(Arrays.asList(params.getText("trafico.group.request.number").split(",")));
            // Cantida de destinos 
            Integer nroDestino = Integer.parseInt(params.getText("trafico.target.number"));
            // Capacidad minima en Gb/s
            Integer capacityFrom = Integer.parseInt(params.getText("trafico.request.capacity.min"));
            // Capacidad Maxima en Gb/s
            Integer capacityTo = Integer.parseInt(params.getText("trafico.request.capacity.max"));
            // NO se para que uso
            String pathSecuencia = params.getText("trafico.path.secuencia");
            String secuencePath = params.getText(" trafico.path.secuenciaNueva");
                    
            // Si las secuencias generadas son diferentes o no
            Boolean distintasSecuencias = Boolean.valueOf(params.getText("trafico.request.secuencia.distintas"));
            
            // Parametros para el algoritmo genetico
            // Tamano de la poblacion
            Integer cp = Integer.parseInt(params.getText("genetico.tamano.de.la.poblacion"));
            // Numero de generaciones
            Integer cg = Integer.parseInt(params.getText("genetico.numero.de.generaciones"));
            // Tamanho del elitismo
            Integer ce = Integer.parseInt(params.getText("genetico.tamano.del.elitismo"));
            // Probabilidad de mutacion
            Double Ps = Double.parseDouble(params.getText("genetico.probabilidad.de.mutacion"));

            //Integer cantidadReq = Integer.parseInt(params.getText("trafico.group.request.number"));
            

            // Extrae el grafo definido en cada archivo por su nombre para cada red
            for (String red : redes) {
                
                // Lectura del archivo de la red
                Network network = new Network(filePath, red);
                
                 // Calcula el shortest path 
                KShortestPath.generate(network, k);
                
                // Carga del Shortest Path Calculado
                Map<String, List<List<DefaultWeightedEdge>>> shortestPaths = KShortestPath.load(network);
                


                // Para cada R
                for (String solicitud : requestsNumber) { 
                    // Valor actutal del request
                    Integer actualRequest = Integer.valueOf(solicitud);
                    
                    // Genero el trafico para todo
                    GenerarTrafico<String, DefaultWeightedEdge> generadorBusiness = new GenerarTrafico<>();

                    // Genera solicitudes
                    List<Grupo<String>> grupoSolicitudes = generarTrafico(generadorBusiness, actualRequest, cantidadGrupo, nroDestino, capacityFrom,
                            capacityTo, cantidadDeSecuencias, pathSecuencia, network, distintasSecuencias);
                    System.out.println("");
                    System.out.println("Trafico generado...");

                    // Por cada algoritmo a ser evaluado
                    for (String algoritmo : algoritmos) { 
                        
                        // Si el algoritmo es LRG o SALRG calcular DasRas
                        if (algoritmo.compareTo("LRG") == 0 || algoritmo.compareTo("SALRG") == 0) {
                            
                            
                            //System.out.println("");
                            //System.out.println("Calculando algoritmo heuristico...");

                            // Deberia devolver el summary, la funcion debe ser el wrap del esquema de la simulacion numerica
                            //SummaryTotal dasras = calcularDasras(solicitud, network, shortestPaths, algoritmo, pathSecuencia, secuencePath);
                            
                            System.out.println("");
                            System.out.println("Red: " + red + ", Solicitud:" + solicitud + ", Algoritmo: "+ algoritmo);
                            System.out.println("Calculando algoritmo heuristico...");
                            for (Grupo<String> R : grupoSolicitudes) {
                                Long initialTime = System.currentTimeMillis();
                                SequenceResult solucion = heuristico(R, network, shortestPaths, algoritmo, pathSecuencia, secuencePath);
                                Long finalTime = System.currentTimeMillis();
                                Long time = (finalTime - initialTime);  
                                System.out.println("Tiempo en ms: " + time);
                                System.out.println("Individuo ganador");
                                System.out.println(algoritmo);
                                System.out.println(solucion);
                                System.out.println(" ");
                                // Call Garbage collector
                                System.gc();
                                
                            }

                        } else {
                            // Calcular algoritmo genetico
                            //SequenceResult solucion = algoritmoGenetico(algoritmo, multicastRequest, distintasSecuencias, cantidadDeSecuencias);
                            System.out.println("");
                            System.out.println("Red: " + red + " Solicitud:" + solicitud + " Algoritmo: "+ algoritmo);
                            System.out.println("Calculando algoritmo genetico...");

                            // Por cada grupo de solicitud multicast
                            

                                List<SequenceResult> bestAlgoritmoGenetico = new ArrayList<>();

                                // Deberia devolver el summary, la funcion debe ser el wrap del esquema de la simulacion numerica
                                // CAMBIAR A 30
                                // TODO: AGREGAR EL PARAMETRO DE CORRIDAS INDEPENDIENTES
                                // EL TIEMPO QUE APARECE ES EL TIEMPO DE LAS 30 CORRIDAS
                                for(int i=0; i < 30; i++) {
                                    //System.out.println(" ");
                                    //System.out.println("Entre al ciclo de 30 / " + i);
                                   
                                    
                                    List<SequenceResult> bestAlgoritmoGeneticoDelGrupo = new ArrayList<>();
                                    int z = 1;
                                    for (Grupo<String> R : grupoSolicitudes) {
                                        Long initialTime = System.currentTimeMillis();
                                        //System.out.println("Entre al ciclo de grupos " + z);
                                        SequenceResult solucion = algoritmoGenetico(R, cp, ce, cg, Ps, algoritmo, distintasSecuencias, network, actualRequest);
                                        //System.out.println(solucion); 
                                        Long finalTime = System.currentTimeMillis();
                                        Long time = (finalTime - initialTime);  
                                        System.out.println("Tiempo en ms: " + time);
                                        System.out.println(solucion);
                                        bestAlgoritmoGeneticoDelGrupo.add(solucion);
                                        z++;
                                        // Call Garbage collector
                                        System.gc();
                                    }
                                    
                                    /***
                                    // Tengo que encontrar el promedio, no el mejor.
                                    SequenceResult promedio = new SequenceResult();
                                    promedio.setCurrentIndexFS(0);
                                    promedio.setTimeRunning(0.0);
                                    
                                    for (SequenceResult tmp : bestAlgoritmoGeneticoDelGrupo) {
                                        // TimeRunning
                                        promedio.setTimeRunning(tmp.getTimeRunning() + promedio.getTimeRunning());
                                        
                                        // currentIndexFS
                                        promedio.setCurrentIndexFS(tmp.getCurrentIndexFS() + promedio.getCurrentIndexFS());
                                        
                                        // numberSuccess
                                        promedio.setNumberSuccess(tmp.getNumberSuccess() + promedio.getNumberSuccess());
                                        
                                        // bvtSequence
                                        // Checkear
                                        promedio.setBvtSequence( (tmp.getBvtSequence() / tmp.getNumberSuccess() ) + promedio.getBvtSequence());
                                    }
                                    
                                    promedio.setTimeRunning(promedio.getTimeRunning() / cantidadGrupo);
                                    promedio.setCurrentIndexFS(promedio.getCurrentIndexFS() / cantidadGrupo);
                                    promedio.setNumberSuccess(promedio.getNumberSuccess() / cantidadGrupo);
                                    promedio.setBvtSequence(promedio.getBvtSequence() / cantidadGrupo);   
                                    ***/
                                    
                                    // Borrar despues
                                    bestAlgoritmoGenetico.add(getFittest(bestAlgoritmoGeneticoDelGrupo));  
                                    //bestAlgoritmoGenetico.add(promedio);  
                                }

                                // Encontrar el mejor individuo de los 30
                                //SequenceResult individuo_ganador = getFittest(bestAlgoritmoGenetico);
                                SequenceResult individuo_ganador = getFittest(bestAlgoritmoGenetico);
                                System.out.println("Individuo ganador");
                                System.out.println(algoritmo);
                                System.out.println(individuo_ganador);  
                                System.out.println(" ");  
                        }
                    }
                }
            }
	}
        
    
       
          /*Entrada: 
                //  Conjunto de Peticiones R,           GRUPO
                //  Grafo de red G, 
                //  Tamaño de la Poblacion cp,          TIMES, 
                //  Tamaño del Elitismo ce, 
                //  Numero de Generaciones cg, 
                //  Contador de convergencia cc,        10 GENERACIONES
            Salida: Mejor individuo MRMLSA */
    
    
    // PARA EL MISMO ARCHIVO GNEERO 30 VECES
    private static SequenceResult algoritmoGenetico(Grupo<String> R, Integer cp, Integer ce, Integer cg, Double Ps, String algoritmo, 
            Boolean distintasSecuencias, Network network, Integer actualRequest) { 
        
            //SequenceResult p_best = getFittest(resultadoPoblacionInicial);
            //int p_best_position = resultadoPoblacionInicial.indexOf(p_best);
            //Secuencia p_best_value = poblacionInicial.get(p_best_position);
            Boolean firstTime = true;
            Boolean firstTimePbest = true; 

            // Genero mi poblacion inicial
            List<Secuencia> poblacionInicial = generarPoblacion(R, distintasSecuencias, cp);
            
            // Evaluo los resutados de mi poblacion inicial
            List<SequenceResult>  resultadoPoblacionInicial = calcularResultadoPoblacion(algoritmo, R, poblacionInicial, network);
            
            // pbest = pbestgeneracional
            // El mejor individuo de la seleccion incial, se guarda como mejor individuo generacional.
            SequenceResult p_best = getFittest(resultadoPoblacionInicial);
            int p_best_position = resultadoPoblacionInicial.indexOf(p_best);
            Secuencia p_best_value = poblacionInicial.get(p_best_position);
            

           
            List<Secuencia> nuevaPoblacion = new ArrayList<>(cp);
            List<SequenceResult> resultadoNuevaPoblacion = new ArrayList<>(cp);
            
            // mientras i < cg hacer
            // Si el mejor individuo no mejora en 10 generaciones, terminar.
            
            //  Bandera del CONTADOR DE CONVERGENCIA
            // K 
            // Si mi CONTADOR DE CONVERGENCIA < a CC
            // Agregar una bandera si el pbset no mejora en los prpoximos 10
            // K = Contador de convergencia
            int k = 0;
            
            for(int i=0; i < cg; i++) {
                
                // is i es menor a cg y k menor a cc = 1-
                // REEMPLAZAR 10 POR LA VARIABLE CC   
                if (i < cg && k < 10) {
                    // El algoritmo no esta estancado
                    // Si el algoritmo corre por primera vez
                    //if (firstTimePbest == true){
                    //    firstTimePbest = false;
                    //} else {
                    //    p_best = getFittest(resultadoPoblacionInicial);
                    //    p_best_position = resultadoPoblacionInicial.indexOf(p_best);
                    //    p_best_value = poblacionInicial.get(p_best_position);
                    //}
                    
                    // Si es la primera vez que entramos al ciclo
                    // Creo dos arrays vacios
                    if (firstTime == true) {

                        firstTime = false;
                        // Inicializa el array nuevaPoblacion con items vacios
                        for (int ca = 0; ca < cp; ca++) {
                            Secuencia secuencia = new Secuencia(cp);
                            nuevaPoblacion.add(secuencia);
                        } 

                        // REEMPLAZA EL PRIMER VALOR CON P_BEST // ELITISMO
                        nuevaPoblacion.set(0, p_best_value);

                        // Inicializa el array resultadoNuevaPoblacion con items vacios
                        for (int car = 0; car < cp; car++) {
                            SequenceResult secuencia = new SequenceResult();
                            resultadoNuevaPoblacion.add(secuencia);
                        }

                        // REEMPLAZA EL PRIMER VALOR CON P_BEST // ELITISMO
                        resultadoNuevaPoblacion.set(0, p_best);

                    } 
                    
                    // Recorro toda la poblacion, conf aparte
                    // mientras cd < cp − ce hacer
                    // Se hace desde 1 porque el valor 0 corresponde a Pbest 
                    for (int j=0; j < cp - ce; j++) {
                        //System.out.println("Entre al ciclo de tamano de poblacion cp " + j);
                        // SELECCION SIN REEMPLAZO
                        // (Pi[a],Pi[b]) ← Seleccionar dos individuos padres de Pi;
                        //System.out.println(poblacionInicial);
                        Secuencia indiv1 = getPadres(poblacionInicial, algoritmo, R, network); // TODO: Ver como traer los mejores candidatos sin repetir
                        Secuencia indiv2 = getPadres(poblacionInicial, algoritmo, R, network);

                        // x ← Cruzar los individuos padres Pi[a] y Pi[a];
                        Secuencia x = crossover(indiv1, indiv2, actualRequest);

                        // y ← Mutar individuo x con tasa ps;
                        Secuencia y = mutate(x, Ps);

                        // yfitness ← Calcular fitness (individuo_mutado, G, R);
                        SequenceResult y_fitness = getFitness(y, network, R, algoritmo);
                        //System.out.println(y_fitness);

                        // Pnew ← Pnew ∪ y;    
                        nuevaPoblacion.set(j + ce, y);
                        resultadoNuevaPoblacion.set(j + ce, y_fitness);

                        // cd ← cd+1;
                        // Variable J
                    }
                    
                    // Pi = Pnew;
                    poblacionInicial = nuevaPoblacion;
                    resultadoPoblacionInicial = resultadoNuevaPoblacion;
                    // Actualizar mejor individuo
                    SequenceResult pbest_temporal = getFittest(resultadoPoblacionInicial);

                    
                    
                    // pbest_temporal es mejor p_best
                    
                    // Si pbestTemporal es igual a pbest, esta estancado
                    if (pbest_temporal.equals(p_best)){
                        // Estancado
                        k = k + 1;
                    
                    } else {
                        
                        SequenceResult mejor = fittest(pbest_temporal, p_best);
                        
                        if (pbest_temporal.equals(mejor)){
                            p_best = pbest_temporal;
                            k = 0; //Resetea 
                        } else {
                            k = k + 1;
                        }
                    }
                    
                    
                
                } else {
                    // Se estanco el algoritmo
                    System.out.println("Se estanco");
                    return p_best;
                }
            }
            
            // Retornar Mejor individuo de Pi ;
            SequenceResult p_best_generation = getFittest(resultadoPoblacionInicial);
            return p_best_generation;
        }
        
        
        
        
    // Mutar utilizando swap
    // MUTACION POR INSERT
    
    public static Secuencia mutate(Secuencia indiv, double mutationRate) {
        //mutationRate = 0.10;
    	Random random = new Random();
    	Integer valor1, valor2;
        
    	for (int i = 0; i < indiv.size(); i++) {
            //Aplicar la mutacion de acuerdo a la tasa
            if (Math.random()<= mutationRate) {
                    int randomPos = random.nextInt(indiv.size()) ;

                    valor1 = indiv.getValue(i);
                    valor2 = indiv.getValue(randomPos);

                    //Aplicar Swap entre ellos puesto que no puede haber valores repetidos
                    indiv.setValue(i, valor2);
                    indiv.setValue(randomPos, valor1);
            }
    	}
        
        return indiv;
    }
    
    
    
    
    
    
    /*
    public static Secuencia mutate(Secuencia indiv, double mutationRate) {
    Random random = new Random();

        for (int i = 0; i < indiv.size(); i++) {
            // Apply mutation with a certain probability
            if (Math.random() <= mutationRate) {
                int pos1 = i;
                int pos2 = random.nextInt(indiv.size());

                // Select two non-adjacent positions in the sequence
                while (Math.abs(pos2 - pos1) <= 1) {
                    pos2 = random.nextInt(indiv.size());
                }

                // Swap the values if they are different
                int value1 = indiv.getValue(pos1);
                int value2 = indiv.getValue(pos2);
                if (value1 != value2) {
                    indiv.setValue(pos1, value2);
                    indiv.setValue(pos2, value1);
                }
            }
        }

        return indiv;
    }
    */

        
    // CrossOver
    private static Secuencia crossover(Secuencia indiv1, Secuencia indiv2, Integer actualRequest) {
        Secuencia child = new Secuencia(actualRequest);

        // Tomar las posiciones inicial y final del individuo
        //int posIni = (int) (Math.random() * indiv1.size());
        //int posFin = (int) (Math.random() * indiv1.size());
        
        // Generate initial values for posIni and posFin
        int posIni = (int) (Math.random() * indiv1.size());
        int posFin = (int) (Math.random() * indiv1.size());

        // Ensure that posFin is greater than posIni
        /**
        if (posIni > posFin) {
            int temp = posIni;
            posIni = posFin;
            posFin = temp;
        }

        // Ensure that the range between posIni and posFin covers at least 2 positions in the sequence
        while (posFin - posIni < 2) {
            posIni = (int) (Math.random() * indiv1.size());
            posFin = (int) (Math.random() * indiv1.size());

            if (posIni > posFin) {
                int temp = posIni;
                posIni = posFin;
                posFin = temp;
            }
        }
        **/

        // Recorrer el individuo 1
        for(int i=0; i<indiv1.size();i++) {
                //Si posIni es menor a posFin
                if (posIni < posFin && i > posIni && i < posFin) {
                        child.setValue(i, indiv1.getValue(i));
                }
                //Si posIni es mayor a posFin
                else if(posIni > posFin) {
                        if(!(i<posIni && i>posFin)) {
                                child.setValue(i, indiv1.getValue(i));
                        }
                }
        }

        //recorrer el indiv2
        for (int i=0; i < indiv2.size();i++) {
                // Si el child no tiene aun el gen agregarlo
                if (!child.containSecuencia(indiv2.getValue(i))) {
                        //Recorrido hasta encontrar los lugares libres para agregar el gen
                        for(int j = 0; j < child.size();j++) {
                                //Si se encuentra el lugar libre se agregar el gen
                                if (child.getValue(j)==null || child.getValue(j)==-1) {
                                        child.setValue(j,indiv2.getValue(i));
                                        break;
                                }
                        }
                }
        }

        return child;
    }
    
    // Equivalente a ProccesSequenceRequest / OK
    public static SequenceResult getFitness(Secuencia y, Network G, Grupo<String> R, String algoritmo) {

        ApplicationParams params = new ApplicationParams();
        params.init();

        Integer frecuencySlotsSize = Integer.parseInt(params.getText("trafico.fs.size"));
        Double capacityNetwork = Double.parseDouble(params.getText("trafico.network.capacity"));
        Integer guardBand = Integer.parseInt(params.getText("trafico.guardBand.size"));
        Double threshold = Double.parseDouble(params.getText("trafico.gamma.value"));

        Map<String, List<List<DefaultWeightedEdge>>> shortestPaths = KShortestPath.load(G);
        Map<String, List<FrecuencySlot<String>>> frecuencySlots = new HashMap<>();


        // Se utiliza algoritmo LGR o SALRG
        ProcessBusiness<String, DefaultWeightedEdge> bean = new ProcessBusiness<>();
        SequenceResult<String, DefaultWeightedEdge> resultSec = bean.proccessSequenceRequest(R, y, 
                shortestPaths, G.getGraph(), frecuencySlots, capacityNetwork, guardBand, threshold, 
                frecuencySlotsSize, algoritmo);


        return resultSec;
    }


    
    // UTILS
    // Obtener una poblacion de forma aleatoria
    public static SequenceResult getRandomPoblacion(List<SequenceResult> poblacion ) {
        Random rand = new Random();
        SequenceResult randomElement = poblacion.get(rand.nextInt(poblacion.size()));
        return randomElement;
    }

    // TODO: ELEGIR EL MEJOR. 
    // Es una funcion de seleccion binaria sin reemplazo, se seleccionan dos padres de forma
    // aleatoria y se comparan entre si, se retorna el mejor individuo.
    public static Secuencia getPadres(List<Secuencia> poblacion, String algoritmo, Grupo<String> grupo, Network network ) {

        Random rand = new Random();
        Secuencia randomElement = poblacion.get(rand.nextInt(poblacion.size()));
        //SequenceResult resultSec_1 = getFitness(algoritmo, grupo, randomElement );
        SequenceResult resultSec_1 = getFitness(randomElement, network, grupo, algoritmo);

        Secuencia returnValue = randomElement;

        Random rand_2 = new Random();
        Secuencia randomElement_2 = poblacion.get(rand_2.nextInt(poblacion.size()));
        //SequenceResult resultSec_2 = getFitness(algoritmo, grupo, randomElement_2 );
        SequenceResult resultSec_2 = getFitness(randomElement_2, network, grupo, algoritmo);


        SequenceResult child = fittest(resultSec_1, resultSec_2);
        // Ver como el child equivale a RandomElemen o randomElement_2
        if (child.equals(resultSec_1)) {
            // child is equal to resultSec_1
            returnValue = randomElement;
        } else if (child.equals(resultSec_2)) {
            // child is equal to resultSec_2
            returnValue = randomElement_2;
        }

        return returnValue;


    }


    private static SequenceResult fittest( SequenceResult seq1, SequenceResult seq2) {
        // Agarro un individuo aleatorio de la poblacion actual como fittest
        SequenceResult fittest = seq1;
        SequenceResult valorEvaluado = seq2;

        // Cuando tengo 2 individuos, priemro testeo el bloqueo.
       if (fittest.getNumberSuccess().compareTo(valorEvaluado.getNumberSuccess()) == 0 ) {
           // Si hay emptate entre el minHIFS
           if (fittest.getCurrentIndexFS().compareTo(valorEvaluado.getCurrentIndexFS()) == 0 ) { 

               // Si hay empate entre transpondedores
               if (fittest.getBvtSequence().compareTo(valorEvaluado.getBvtSequence()) == 0 ) { 
                   // TODO: hacer que sean random
                   fittest = fittest;
               } else if (fittest.getBvtSequence() < valorEvaluado.getBvtSequence()) {
                   fittest = fittest;
               } else {
                   fittest = valorEvaluado;
               }

           } else if (fittest.getCurrentIndexFS() < valorEvaluado.getCurrentIndexFS() ) {
               fittest = fittest;
           } else {
               fittest = valorEvaluado;
           }

       } else if (fittest.getNumberSuccess() > valorEvaluado.getNumberSuccess() ) {
           fittest = fittest;
       } else {
           fittest = valorEvaluado;
       }
   
       


        return fittest;
    }


    private static SequenceResult getFittest( List<SequenceResult> poblacion) {
        // Agarro un individuo aleatorio de la poblacion actual como fittest
        SequenceResult fittest = getRandomPoblacion(poblacion);

        // ACA HAY UN TEMA: SI EL VALOR RANDOM SE EVALUA CON EL VALOR ACTUAL VA A DAR COMO FITTEST
        // TENGO QUE QUITAR O BYPASSEAR DE LA LISTA
        for (Integer i = 0; i < poblacion.size(); i++) {
            SequenceResult valorEvaluado = poblacion.get(i);
            

            
             // Cuando tengo 2 individuos, priemro testeo el bloqueo.
            if (fittest.getNumberSuccess().compareTo(valorEvaluado.getNumberSuccess()) == 0 ) {
                // Si hay emptate entre el minHIFS indice maximo(?)                                                             // Max max indice maximo
                if (fittest.getCurrentIndexFS().compareTo(valorEvaluado.getCurrentIndexFS()) == 0 ) { 

                    // Si hay empate entre transpondedores
                    if (fittest.getBvtSequence().compareTo(valorEvaluado.getBvtSequence()) == 0 ) {                 // el menor
                        // TODO: hacer que sean random
                        fittest = fittest;
                    } else if (fittest.getBvtSequence() < valorEvaluado.getBvtSequence()) {
                        fittest = fittest;
                    } else {
                        fittest = valorEvaluado;
                    }

                } else if (fittest.getCurrentIndexFS() < valorEvaluado.getCurrentIndexFS() ) {
                    fittest = fittest;
                } else {
                    fittest = valorEvaluado;
                }

            } else if (fittest.getNumberSuccess() > valorEvaluado.getNumberSuccess() ) {
                fittest = fittest;
            } else {
                fittest = valorEvaluado;
            }
           
            
        }

        return fittest;
    }

    // Equivalente a ProcessSequenceRequest
    private static List<SequenceResult> calcularResultadoPoblacion(String algoritmo, Grupo<String> grupo, List<Secuencia> poblacion, Network network) {            
        List<SequenceResult> listaResultadoPoblacion = new ArrayList<>();

        for (Integer i = 0; i < poblacion.size(); i++) { 
            //SequenceResult resultSec = getFitness(algoritmo, grupo, poblacion.get(i) );
            SequenceResult resultSec = getFitness(poblacion.get(i), network, grupo, algoritmo);

            listaResultadoPoblacion.add(resultSec);
        }

        return listaResultadoPoblacion;

    }


    // Genera las secuencias iniciales aleatoriamente guaidas - GA
    private static List<Secuencia> generarPoblacion(Grupo<String> grupo, Boolean distintasSecuencias, Integer times) {
        SequenceTraffic<List<Secuencia>> poblacionBusiness = new SequenceTraffic<>();
        poblacionBusiness.generarPoblacion(grupo, distintasSecuencias, times);
        return poblacionBusiness.getSecuenciaList();
    }

    public static List<Grupo<String>> generarTrafico (GenerarTrafico<String, DefaultWeightedEdge> generadorBusiness, Integer cantidadReq,
            Integer cantidadGrupo, Integer nroDestino, Integer capacityFrom, Integer capacityTo, Integer times,
            String pathSecuencia, Network network, Boolean distintasSecuencias) {
        List<Grupo<String>> grupoSolicitudes = generadorBusiness.generar(network.getGraph(), cantidadReq, cantidadGrupo, nroDestino,
            capacityFrom, capacityTo, times, pathSecuencia, network.getFileName(),
            distintasSecuencias);

         // Calcular Secuencias puramente aleatoria
        for (Grupo<String> grupo : grupoSolicitudes) {
            // Genera secuencias aleatorias de solicitudes
            SequenceTraffic<String> secuenciaBusiness = new SequenceTraffic<>();
            secuenciaBusiness.generarSecuencia(grupo, distintasSecuencias, times);
            List<Secuencia> secuencias = secuenciaBusiness.getSecuenciaList();
            grupo.setShuffledSequence(secuencias);
            writeTrafico(grupo, pathSecuencia, network.getFileName(), cantidadReq);
        }
        return grupoSolicitudes;
    }

    /***
    public static SummaryTotal calcularDasras(String numero, Network network, Map<String, List<List<DefaultWeightedEdge>>> shortestPaths,
            String algoritmo, String pathSecuencia, String secuencePath) throws FileMapperException {
        boolean isfirst = true;
        SummaryTotal bestSummary = new SummaryTotal();

        // Corridas  Montecarlo)?=
        for (int i=0; i < 1; i++) {
            Integer cantidadRequest = Integer.parseInt(numero);
            ProcessBusiness<String, DefaultWeightedEdge> bean = new ProcessBusiness<>();
            GroupResult<String, DefaultWeightedEdge> result = bean.processGroup(network.getGraph(),
                            network.getFileName(), shortestPaths, algoritmo, cantidadRequest);

            if (isfirst) {
                    bestSummary = result.getSummary();
                    isfirst = false;
            }
            if (bestSummary.compareTo(result.getSummary()) > 0) {
                    bestSummary = result.getSummary();
                    //writeResult(secuencePath, network, result.getSubtreeGroupResult(), algoritmo, cantidadRequest);
                    //moverBest(pathSecuencia, "C:\\Users\\jony\\Desktop\\dev\\elena\\resultados\\best\\", network.getFileName(), numero);
                    double diference = Math.abs((double) 49.5 - bestSummary.getHighestIndexFS());
                    if (diference < 0.2) {
                            break;
                    }
            }
        }

        System.out.println("Resultado del mejor");
        System.out.println("Algoritmo: " + bestSummary.getAlgoritmo());
        System.out.println("Request: " + bestSummary.getRequest());
        System.out.println("HighestIndexFs: " + bestSummary.getHighestIndexFS());
        System.out.println("TimeRunning: " + bestSummary.getTimeRunning());
        System.out.println("BVT Usage: " + bestSummary.getBvtUsage());
        System.out.println("NumberSuccess: " + bestSummary.getNumberSuccess());
        return bestSummary;
    }
    ***/
    public static SequenceResult heuristico(Grupo<String> R, Network G, Map<String, List<List<DefaultWeightedEdge>>> shortestPath,
            String algoritmo, String pathSecuencia, String secuencePath) throws FileMapperException {

        // Para cada grupo        
        ApplicationParams params = new ApplicationParams();
        params.init();

        Integer frecuencySlotsSize = Integer.parseInt(params.getText("trafico.fs.size"));
        Double capacityNetwork = Double.parseDouble(params.getText("trafico.network.capacity"));
        Integer guardBand = Integer.parseInt(params.getText("trafico.guardBand.size"));
        Double threshold = Double.parseDouble(params.getText("trafico.gamma.value"));

        Map<String, List<List<DefaultWeightedEdge>>> shortestPaths = KShortestPath.load(G);
        Map<String, List<FrecuencySlot<String>>> frecuencySlots = new HashMap<>();
        
        List<SequenceResult> resultados = new ArrayList<>();
        
        for (int ca = 0; ca < R.getShuffledSequence().size(); ca++) {
            SequenceResult secuencia = new SequenceResult();
            resultados.add(secuencia);
        } 
        
        // Procesamos las 300000 secuencias
        int contaZ = 0;
        for (Secuencia y : R.getShuffledSequence()) {   
            // Se utiliza algoritmo LGR o SALRG
            ProcessBusiness<String, DefaultWeightedEdge> bean = new ProcessBusiness<>();
            SequenceResult<String, DefaultWeightedEdge> resultSec = bean.proccessSequenceRequest(R, y, 
                    shortestPaths, G.getGraph(), frecuencySlots, capacityNetwork, guardBand, threshold, 
                    frecuencySlotsSize, algoritmo);
            
            resultados.add(resultSec);
            
            contaZ = contaZ + 1;
            
            if (contaZ % 50000 == 0) {
                System.out.println(contaZ);
                System.gc();
            }
            
            
            //System.gc();
        }
        System.gc();
        
        SequenceResult p_best_generation = getFittest(resultados);
        return p_best_generation;
    }
    
    
    
    
    
        private static void writeResult(String path, Network network,
                    List<SubtreeGroupResult<String, DefaultWeightedEdge>> result, String algoritmo, Integer requestNumber) {
            FileWriter fw = null;

            ObjectMapper mapper = new ObjectMapper();
            mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
            for (SubtreeGroupResult<String, DefaultWeightedEdge> rs : result) {
                    try {
                            fw = openFile(path, network.getFileName(), rs.getGroup().toString(), algoritmo, requestNumber);
                            for (SubtreeSequenceResult<String, DefaultWeightedEdge> sequence : rs.getSubtreeSequence()) {
                                    String grupoInString = mapper.writeValueAsString(sequence);
                                    fw.write(grupoInString);
                                    fw.write("\n");
                            }
                    } catch (IOException e) {
                            e.printStackTrace();
                    } finally {
                            closeFile(fw);
                    }
            }

    }

    private static FileWriter openFile(String pathN, String networkName, String key, String algoritmo,
                    Integer requestNumber) {
            String path;
            // = new java.io.File("").getAbsolutePath();
            path = pathN + networkName + "_group_" + key + "_nrorequest_" + requestNumber + "_" + algoritmo + "_result";
            FileWriter fw = null;
            try {
                    fw = new FileWriter(path, false);
            } catch (FileNotFoundException e) {
                    e.printStackTrace();
            } catch (IOException e) {
                    e.printStackTrace();
            }
            return fw;
    }

    private static void closeFile(FileWriter fw) {
            if (null != fw) {
                    try {
                            fw.close();
                    } catch (IOException e) {
                            e.printStackTrace();
                    }
            }
    }

    private static void moverBest(String origenArchivo, String destinoArchivo, String networkFileName,
                    String nroRequest) {
            try {
                    for (int i = 1; i <= 10; i++) {
                            String fileName = networkFileName + "_grupo_" + i + "_nrorequest_" + nroRequest;
                            File ficheroCopiar = new File(origenArchivo, fileName);
                            File ficheroDestino = new File(destinoArchivo, fileName);

                            Path destinoPath = Paths.get(ficheroDestino.getAbsolutePath());
                            Path origenPath = Paths.get(ficheroCopiar.getAbsolutePath());

                            Files.copy(origenPath, destinoPath, StandardCopyOption.REPLACE_EXISTING);
                    }
            } catch (IOException e) {
                    e.printStackTrace();
            }
    }


    // WriteResult
    private static void writeTrafico(Grupo<String> grupo, String path, String networkName, Integer cantidadRequest) {
        FileWriter fw = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
                fw = openFileTrafico(grupo, path, networkName, cantidadRequest);
                String grupoInString = mapper.writeValueAsString(grupo);
                fw.write(grupoInString + "\n");
        } catch (IOException e) {
                e.printStackTrace();
        } finally {
                closeFileTrafico(fw);
        }
    }

    private static void closeFileTrafico(FileWriter fw) {
            if (null != fw) {
                    try {
                            fw.close();
                    } catch (IOException e) {
                            e.printStackTrace();
                    }
            }
    }

    private static FileWriter openFileTrafico(Grupo<String> grupo, String pathN, String networkName, Integer cantidadRequest) {
		String path = pathN + networkName + "_grupo_" + grupo.getNro() + "_nrorequest_" + cantidadRequest;

		try {
			return new FileWriter(path, false);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
    
}
