package py.una.fp.eon.dasras.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import py.una.fp.eon.core.FrecuencySlot;
import py.una.fp.eon.core.FrecuencySlotStatus;
import py.una.fp.eon.core.GroupResult;
import py.una.fp.eon.core.Grupo;
import py.una.fp.eon.core.Secuencia;
import py.una.fp.eon.core.SequenceResult;
import py.una.fp.eon.core.Solicitud;
import py.una.fp.eon.core.Subtree;
import py.una.fp.eon.core.SubtreeGroupResult;
import py.una.fp.eon.core.SubtreeSequence;
import py.una.fp.eon.core.Summary;
import py.una.fp.eon.core.SummaryGeneral;
import py.una.fp.eon.core.SummarySequence;
import py.una.fp.eon.core.SummaryTotal;
//import py.una.fp.eon.core.utils.CSVUtils;
//import py.una.fp.eon.core.utils.Output;

import py.una.fp.eon.dasras.algorithm.AlgorithmBase;
import py.una.fp.eon.dasras.algorithm.LRGAlgorithm;
import py.una.fp.eon.dasras.algorithm.SALRGAlgorithm;
import py.una.fp.eon.dasras.exceptions.FileMapperException;
import py.una.fp.eon.trafico.property.ApplicationParams;

public class ProcessBusiness<V, E extends DefaultWeightedEdge> {
    
    
    public GroupResult<V, E> processGroup(Graph<V, E> graph, String networkFileName,
                    Map<String, List<List<E>>> shortestPaths, String algoritmo, 
                    Integer requestNumber)

        throws FileMapperException {
            ApplicationParams params = new ApplicationParams();
            params.init();

            Integer frecuencySlotsSize = Integer.parseInt(params.getText("trafico.fs.size"));
            Double threshold = Double.parseDouble(params.getText("trafico.gamma.value"));
            Integer guardBand = Integer.parseInt(params.getText("trafico.guardBand.size"));
            Double capacityNetwork = Double.parseDouble(params.getText("trafico.network.capacity"));
            Integer cantGrupos = Integer.parseInt(params.getText("trafico.group.number"));
            String path = params.getText("trafico.path.secuencia");
            
            Map<Integer, SubtreeSequence<V, E>> subtreeforGroup = new HashMap<>();
            List<SubtreeGroupResult<V, E>> groups = new ArrayList<>();
            
            Double BVTUsage = new Double(0);
            Double totalRunning = new Double(0);
            Double highest = 0.0;
            Integer numberSuccess = 0;
            List<Summary> summaryGroups = new ArrayList<>();

            for (int i = 1; i <= cantGrupos; i++) {
                Map<String, List<FrecuencySlot<V>>> frecuencySlots = new HashMap<>();

                for (E de : (Set<E>) graph.edgeSet()) {
                        List<FrecuencySlot<V>> fs = Arrays.asList(new FrecuencySlot[frecuencySlotsSize]);
                        frecuencySlots.put(de.toString(), fs);
                }

                // Realiza la lectura de networkN6S9_grupo_1_nrorequest_5 (Trafico y secuencia generada)
                Grupo<V> grupo = getSecuencia(path, networkFileName, i, requestNumber);
                //System.out.println(grupo);

                // Procesa la secuencia del grupo y retorna los subtrees con sus resultados
                SubtreeSequence<V, E> subtreeforSequence = processSequence(grupo, shortestPaths, (Graph<V, E>) graph,
                                frecuencySlots, capacityNetwork, guardBand, threshold, frecuencySlotsSize, algoritmo);

                subtreeforGroup.put(grupo.getNro(), subtreeforSequence);
                SubtreeGroupResult<V, E> subtreeGrupo = new SubtreeGroupResult<>(networkFileName, i, subtreeforSequence.getSubtreeforSequence());
                groups.add(subtreeGrupo);
                
                // Validar resultados
                BVTUsage = BVTUsage + subtreeforSequence.getSummary().getBvtUsage();
                totalRunning = totalRunning + subtreeforSequence.getSummary().getTimeRunning();
                highest = highest + subtreeforSequence.getSummary().getHighestIndexFS();
                if (subtreeforSequence.getSummary().getNumberSuccess() == null){
                    System.out.println("null");
                }
                numberSuccess = numberSuccess + subtreeforSequence.getSummary().getNumberSuccess();
                
                summaryGroups.add(subtreeforSequence.getSummary());
            }

                
            // DEPENDE DE LA CANTIDAD DEL GRUPO TOTAL
            Double highestAverage = (double) highest / (double) cantGrupos;
            Double averageBVTUsage = BVTUsage / (double) cantGrupos;
            Double totalRunningAverage = totalRunning / (double) cantGrupos;
            Integer totalNumberSuccess = (int) Math.floor(numberSuccess / cantGrupos);
            
            Summary total = new Summary(0, highestAverage, averageBVTUsage, totalRunningAverage, totalNumberSuccess);
            summaryGroups.add(total);
            
            // Escribe en archivo network_nrorequest_5_LRG_summary.csv
            //writeSummary(path, networkFileName, algoritmo, requestNumber, summaryGroups);
            
            SummaryTotal sTotal = new SummaryTotal(algoritmo, requestNumber, total);
            // Agrega al archivo networkRED_reporte.csv
            //addSummary(path, networkFileName, sTotal);
            
            GroupResult<V, E> summarySubtree = new GroupResult<>(sTotal, groups);
            
            return summarySubtree;
        }
    
    public GroupResult<V, E> processGroup(Graph<V, E> graph, String networkFileName,
                    Map<String, List<List<E>>> shortestPaths, String algoritmo, 
                    Integer requestNumber, Integer indice)

        throws FileMapperException {
            ApplicationParams params = new ApplicationParams();
            params.init();

            Integer frecuencySlotsSize = Integer.parseInt(params.getText("trafico.fs.size"));
            Double threshold = Double.parseDouble(params.getText("trafico.gamma.value"));
            Integer guardBand = Integer.parseInt(params.getText("trafico.guardBand.size"));
            Double capacityNetwork = Double.parseDouble(params.getText("trafico.network.capacity"));
            Integer cantGrupos = Integer.parseInt(params.getText("trafico.group.number"));
            String path = params.getText("trafico.path.secuencia");
            Map<Integer, SubtreeSequence<V, E>> subtreeforGroup = new HashMap<>();

            List<SubtreeGroupResult<V, E>> groups = new ArrayList<>();
            Double BVTUsage = new Double(0);
            Double totalRunning = new Double(0);
            Double highest = 0.0;
            List<Summary> summaryGroups = new ArrayList<>();
            
            Integer numberSuccess = 0;

            // Debe empezar en 1? Si
            for (int i = 1; i <= cantGrupos; i++) {
                Map<String, List<FrecuencySlot<V>>> frecuencySlots = new HashMap<>();

                for (E de : (Set<E>) graph.edgeSet()) {
                        List<FrecuencySlot<V>> fs = Arrays.asList(new FrecuencySlot[frecuencySlotsSize]);
                        frecuencySlots.put(de.toString(), fs);
                }

                // Realiza la lectura de networkN6S9_grupo_1_nrorequest_5 (Trafico y secuencia generada)
                Grupo<V> grupo = getSecuencia(path, networkFileName, i, requestNumber, indice);
                System.out.println(grupo);

                // Procesa la secuencia del grupo y retorna los subtrees con sus resultados
                SubtreeSequence<V, E> subtreeforSequence = processSequence(grupo, shortestPaths, (Graph<V, E>) graph,
                                frecuencySlots, capacityNetwork, guardBand, threshold, frecuencySlotsSize, algoritmo);

                subtreeforGroup.put(grupo.getNro(), subtreeforSequence);
                SubtreeGroupResult<V, E> subtreeGrupo = new SubtreeGroupResult<>(networkFileName, i, subtreeforSequence.getSubtreeforSequence());
                groups.add(subtreeGrupo);
                
                // Validar resultados
                BVTUsage = BVTUsage + subtreeforSequence.getSummary().getBvtUsage();
                totalRunning = totalRunning + subtreeforSequence.getSummary().getTimeRunning();
                highest = highest + subtreeforSequence.getSummary().getHighestIndexFS();
                summaryGroups.add(subtreeforSequence.getSummary());
                numberSuccess = numberSuccess + subtreeforSequence.getSummary().getNumberSuccess();
            }

            Double highestAverage = (double) highest / (double) cantGrupos;
            Double averageBVTUsage = BVTUsage / (double) cantGrupos;
            Double totalRunningAverage = totalRunning / (double) cantGrupos;
            Integer totalNumberSuccess = numberSuccess / cantGrupos;
            Summary total = new Summary(0, highestAverage, averageBVTUsage, totalRunningAverage, totalNumberSuccess);
            
            /*
            System.out.println("******** Resumen *********");
            System.out.println("highestIndexFS: " + highestAverage);
            System.out.println("Average BVT Usage: " + averageBVTUsage + ", TotalRunning: " + totalRunningAverage);
            System.out.println("********           *********");
            */
            
            summaryGroups.add(total);
            
            // Escribe en archivo network_nrorequest_5_LRG_summary.csv
            //'number'	'highestIndexFS'	'timeRunning'	'bvtUsage'	'numberSuccess'
            //writeSummary(path, networkFileName, algoritmo, requestNumber, summaryGroups);
            
            SummaryTotal sTotal = new SummaryTotal(algoritmo, requestNumber, total);
            
            // Agrega al archivo networkRED_reporte.csv
            // 'algorithm'  'request' 'highestIndexFS'	'timeRunning'	'bvtUsage'	
            //addSummary(path, networkFileName, sTotal);
            
            GroupResult<V, E> summarySubtree = new GroupResult<>(sTotal, groups);
            return summarySubtree;
        }

    private SubtreeSequence<V, E> processSequence(Grupo<V> grupo, Map<String, List<List<E>>> shortestPaths,
                    Graph<V, E> graph, Map<String, List<FrecuencySlot<V>>> frecuencySlots, Double capacityNetwork,
                    Integer guardBand, Double threshold, Integer frecuencySlotsSize, String algoritmo) {

            int nroSequenceResult = 1;
            int nroSequence = 1;
            SubtreeSequence<V, E> subtreeSequence = new SubtreeSequence<>();
            Integer minihighestIndexFS = -1;
            Boolean flag = true;
            Double BvtGroup = new Double(0);
            Long initialTime = System.currentTimeMillis();
            Integer secuenciaActual = 1;
            Integer numberSuccess = 0;

            // Equivalente al algoritmo genético
            // Hace 1000 veces esto
            for (Secuencia secuencia : grupo.getShuffledSequence()) {                        
                SequenceResult<V, E> resultSec= proccessSequenceRequest(grupo, secuencia, shortestPaths, graph, frecuencySlots, capacityNetwork, guardBand, threshold, frecuencySlotsSize, algoritmo);
                //System.out.println(resultSec);
                
                BvtGroup = BvtGroup + (resultSec.getBvtSequence() / (double) resultSec.getNumberSuccess());
                //numberSuccess = numberSuccess + resultSec.getNumberSuccess();

                if (flag) {
                        minihighestIndexFS = resultSec.getCurrentIndexFS();
                        secuenciaActual = nroSequence;
                        flag = false;
                }
                
                if (resultSec.getCurrentIndexFS().compareTo(minihighestIndexFS) < 0) {
                        minihighestIndexFS = resultSec.getCurrentIndexFS();
                        secuenciaActual = nroSequence;
                }

                SummaryGeneral summarySeq = new SummaryGeneral(0d, 0d, 0d, 0);
                if (resultSec.getNumberSuccess().compareTo(0) > 0) {
                        summarySeq = new SummaryGeneral(new Double(resultSec.getCurrentIndexFS()), resultSec.getBvtSequence(), 0d, resultSec.getNumberSuccess());
                        nroSequenceResult++;
                } else {
                        summarySeq = new SummaryGeneral(0d, 0d, 0d, 0);
                }

                SummarySequence<V, E> summarySequence = new SummarySequence<>(resultSec.getStatusFSs(), summarySeq);
                subtreeSequence.add(nroSequence, summarySequence);
                nroSequence++;
            }

            Long finalTime = System.currentTimeMillis();
            Long time = finalTime - initialTime;

            // Unidad de medida SEGUNDOS
            Double totalRunning = (double) time / (double) 1000;
            Double BVTUsage = BvtGroup / (double) (nroSequenceResult - 1);
            // Exitoso seria len de grupo.solicitudes * len de suffledSequence - nroSequenceResult - 1
            // Bloqueos = total - exitosos
            //Integer bloqueados = (nroSequence - 1) - (nroSequenceResult - 1);
            Integer success = nroSequenceResult - 1;

            //System.out.println("--- Resultado del grupo ---");
            //System.out.println("Grupo " + grupo.getNro() + ", Minimun highestIndexFS: " + minihighestIndexFS + " Secuencia Actual:" + secuenciaActual);
            //System.out.println("Average BVT Usage: " + BVTUsage + ", Running Time: " + totalRunning);
            //System.out.println("--------------------------- ");


            subtreeSequence.setSummary(grupo.getNro(), BVTUsage, totalRunning, minihighestIndexFS, success);
            return subtreeSequence;
    }

    public SequenceResult<V, E> proccessSequenceRequest(Grupo<V> grupo, Secuencia secuencia, Map<String, List<List<E>>> shortestPaths,
                    Graph<V, E> graph, Map<String, List<FrecuencySlot<V>>> frecuencySlots, Double capacityNetwork,
                    Integer guardBand, Double threshold, Integer frecuencySlotsSize, String algoritmo) {

            SequenceResult<V, E> result = new SequenceResult<>(); 

        Map<String, List<FrecuencySlot<V>>> frecuencySlotsSeq = new HashMap<>();
        for (E de : (Set<E>) graph.edgeSet()) {
                List<FrecuencySlot<V>> fs = Arrays.asList(new FrecuencySlot[frecuencySlotsSize]);
                frecuencySlotsSeq.put(de.toString(), fs);
        }

        // Procesa 1 request a la vez
        for (Integer nroSolicitud : secuencia.getSecuencia()) {
            Solicitud<V> originalRequest = grupo.getSolicitudes().get(nroSolicitud);

            double startTime = System.nanoTime() / 1000000.0;
            // Selecciona el algoritmo para ejecutar la prueba LGR o SA-LGR
            AlgorithmBase<V, E> algorithmResolution = selectMethod(originalRequest, shortestPaths, graph,
                            capacityNetwork, guardBand, threshold, frecuencySlotsSize, frecuencySlotsSeq, result.getCurrentIndexFS(),
                            algoritmo);
            
            // Code to measure execution time
            double endTime = System.nanoTime() / 1000000.0;
            double timeElapsed = endTime - startTime;

            /*
            System.out.println("Impresion del resultado del algoritmo");
            System.out.println(algorithmResolution);
            */

            // Genera los subtrees
            if (algorithmResolution != null && !algorithmResolution.getRequest().isBloqued()) {
                List<Subtree<V, E>> subtrees = new ArrayList<>(algorithmResolution.getSubtreeList());
                frecuencySlotsSeq = algorithmResolution.getFrecuencySlots();
                // Imprime la cantidad de frequency slots utilizados por enlace (x:y).
                // System.out.println(algorithmResolution.getFrecuencySlots());
                if (result.getCurrentIndexFS().compareTo(algorithmResolution.getIndexFS()) < 0) {
                        result.setCurrentIndexFS(algorithmResolution.getIndexFS());
                }
                // Agrega si fue exitoso
                result.addNumberSuccess();
                result.setTimeRunning(timeElapsed); // In Milisecond
                result.addBvtSequence((double) algorithmResolution.getSubtreeList().size());
                FrecuencySlotStatus<V, E> status = new FrecuencySlotStatus<>(subtrees,algorithmResolution.getRequest(), algorithmResolution.getMethodList());
                result.addStatusFSs(status);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Grupo<V> getSecuencia(String pathFile, String networkName, Integer groupNumber, Integer nroRequest, Integer indice)
                    throws FileMapperException {

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

            // JSON from file to Object
            Grupo<V> grupo;
            try {
                    grupo = mapper.readValue(
                                    new File(pathFile + networkName + "_grupo_" + groupNumber + "_nrorequest_" + nroRequest + "_indice_" + indice),
                                    Grupo.class);
            } catch (IOException e) {
                    e.printStackTrace();
                    throw new FileMapperException(e.getCause());
            }

            return grupo;
    }


    @SuppressWarnings("unchecked")
    private Grupo<V> getSecuencia(String pathFile, String networkName, Integer groupNumber, Integer nroRequest)
                    throws FileMapperException {

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

            // JSON from file to Object
            Grupo<V> grupo;
            try {
                    grupo = mapper.readValue(
                                    new File(pathFile + networkName + "_grupo_" + groupNumber + "_nrorequest_" + nroRequest),
                                    Grupo.class);
            } catch (IOException e) {
                    e.printStackTrace();
                    throw new FileMapperException(e.getCause());
            }

            return grupo;
    }        

    // Analiza el tipo de algoritmo necesario y 
    private AlgorithmBase<V, E> selectMethod(Solicitud<V> originalRequest, Map<String, List<List<E>>> shortestPaths,
                    Graph<V, E> graph, Double capacityNetwork, Integer guardBand, Double threshold, Integer frecuencySlotsSize,
                    Map<String, List<FrecuencySlot<V>>> frecuencySlotsSeq, Integer currentIndexFS, String algoritmo) {

           /* System.out.println("------------------------------------");
            System.out.println("Estos parametros recibe el algoritmo");
            System.out.println(originalRequest);
            System.out.println(shortestPaths);
            System.out.println(graph);
            System.out.println(capacityNetwork);
            System.out.println(guardBand);
            System.out.println(threshold);
            System.out.println(frecuencySlotsSize);
            System.out.println(frecuencySlotsSeq);
            System.out.println(currentIndexFS);
            System.out.println(algoritmo);
            System.out.println("------------------------------------");
        */

            if (algoritmo.compareTo("LRG") == 0 || algoritmo.compareTo("GALRG") == 0) {
                    LRGAlgorithm<V, E> lrgResolution = new LRGAlgorithm<V, E>(graph, frecuencySlotsSeq, originalRequest,
                                    capacityNetwork, guardBand, threshold, frecuencySlotsSize);
                    lrgResolution.calculate(shortestPaths, currentIndexFS);
                    return lrgResolution;
            } else {
                    SALRGAlgorithm<V, E> salrgResolution = new SALRGAlgorithm<>(graph, frecuencySlotsSeq, originalRequest,
                                    capacityNetwork, guardBand, frecuencySlotsSize, threshold);
                    salrgResolution.calculate(shortestPaths, currentIndexFS);
                    return salrgResolution;
            }



    }

    
    // 'number'	'highestIndexFS'	'timeRunning'	'bvtUsage'	'numberSuccess'
    // TODO: debo agregar el algoritmo, el request
    /***
    private void writeSummary(String path, String networkName, String algoritmo, 
            Integer requestNumber, List<Summary> summaryGroups) {
        
        FileWriter fw = null;

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        
        String pathN;
        Boolean archivoExiste = false;
        // = new java.io.File("").getAbsolutePath();
        pathN = path + networkName + "_summary.csv";
        
        // Iterar la lista de Summary, y escupir en el grupo.
        List<Output> salida = new ArrayList<>();
        
        for (Summary temp : summaryGroups) {
            //Output value = new Output(temp.getNumber(), temp.getHighestIndexFS(), temp.getTimeRunning(), temp.getBvtUsage(), temp.getNumberSuccess(), requestNumber, algoritmo);
            //salida.add(value);
        }
        

        try {
            
            File f = new File(pathN);
            if(f.exists() && !f.isDirectory()) { 
                // do something
                archivoExiste = true;
            }
            
            //fw = openFile(path, networkName, algoritmo, requestNumber);
            fw = new FileWriter(pathN, false);
            
            if (archivoExiste) {
                String[] fields = {"grupo", "highestIndexFS", "timeRunning", "bvtUsage", "numberSuccess", "request", "algorithm"};
                CSVUtils.write(fw, salida, fields);
                
            } else {
                CSVUtils.write(fw, salida);
            }
  

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            
            if (null != fw) {
                    try {
                            fw.close();
                    } catch (IOException e) {
                            e.printStackTrace();
                    }
            }
        }
    }
    ***/

                

     
	private FileWriter openFileCSV(String pathN, String name, String networkName, boolean append) {
		String path;
		// = new java.io.File("").getAbsolutePath();
		path = pathN + networkName + "_" + name + ".csv";
		FileWriter fw = null;
		try {
			fw = new FileWriter(path, append);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fw;
	}


        //  'algorithm'  'request' 'highestIndexFS'	'timeRunning'	'bvtUsage'
        /***
	private void addSummary(String path, String networkName, SummaryTotal summaryGroups) {
		FileWriter fw = null;

		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

		try {
			fw = openFileCSV(path, "reporte", networkName, true);

			// Field[] fields = SummaryTotal.class.getDeclaredFields();
			List<SummaryTotal> lista = new ArrayList<>();
			lista.add(summaryGroups);
			CSVUtils.write(fw, lista);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
						fw = new FileWriter(path, append);
                    } catch (FileNotFoundException e) {
                            e.printStackTrace();
                    } catch (IOException e) {
                            e.printStackTrace();
                    }
                    return fw;
                    }

	}
        * **/
}
