/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package py.una.fp.eon.core.utils;

import py.una.fp.eon.core.Model;

/**
 *
 * @author funes
 */
public class Output extends Model{
    private Double highestIndexFS; 
    private Double timeRunning; 
    private Double bvtUsage;
    private Integer numberSuccess;
    private Integer request;
    private String algorithm;
    private Integer number; // grupo, 0 es el total
    private Integer grupo;

    public Output(Integer number, Double highestIndexFS, Double timeRunning, Double bvtUsage, Integer numberSuccess, Integer request, String algorithm, Integer grupo ) {
        this.highestIndexFS = highestIndexFS;
        this.timeRunning = timeRunning;
        this.bvtUsage = bvtUsage;
        this.numberSuccess = numberSuccess;
        this.request = request;
        this.algorithm = algorithm;
        this.number = number;
        this.grupo = grupo;
    }
    
    public Output(String algorithm, Integer request, Double highestIndexFS, Double timeRunning, Double bvtUsage) {
        this.algorithm = algorithm;
        this.request = request;
        this.highestIndexFS = highestIndexFS;
        this.timeRunning = timeRunning;
        this.bvtUsage = bvtUsage;
    }

    public Double getHighestIndexFS() {
            return highestIndexFS;
    }

    public Double getTimeRunning() {
            return timeRunning;
    }

    public Double getBvtUsage() {
            return bvtUsage;
    }

    public Integer getNumberSuccess() {
            return numberSuccess;
    }

    public String getAlgorithm() {
            return algorithm;
    }

    public Integer getRequest() {
            return request;
    }

    public Integer getNumber() {
            return number;
    }

    public void setNumber(Integer grupo) {
            this.number = grupo;
    }
    
    public Integer getGrupo() {
            return grupo;
    }
    
    @Override
    public String toStringCSV(char separator) {
            return new StringBuilder().append(getGrupo()).append(separator).append(getNumber()).append(separator).append(getHighestIndexFS()).append(separator)
                            .append(getTimeRunning()).append(separator).append(getBvtUsage()).append(separator).append(getNumberSuccess()).append(separator).append(getRequest()).append(separator).append(getAlgorithm()).toString();
    }
    
    public String toStringCSV2(char separator) {
            return new StringBuilder().append(getAlgorithm()).append(separator).append(getRequest()).append(separator).append(getHighestIndexFS()).append(separator)
                            .append(getTimeRunning()).append(separator).append(getBvtUsage()).toString();
    }
}
