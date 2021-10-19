package com.company;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class C_Means {
    private HashMap<IrisFisher,  ArrayList<Double>> cluster_membership;
    private HashMap<Integer, IrisFisher> centroid;

    public  void loadData(String path) throws IOException {
        cluster_membership = new HashMap<>();
        FileReader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String[] name_coll = bufferedReader.readLine().split(",");
        for(String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
            String[] split_text = line.split(",");
            IrisFisher irisFisher = new IrisFisher();
            HashMap<String, Double> newDate = new HashMap<>();
            for (int col = 0; col < name_coll.length-1; ++col){
                newDate.put(name_coll[col], Double.parseDouble(split_text[col]));
            }
            irisFisher.setSpecies(split_text[4]);
            irisFisher.setFeatures(newDate);
            cluster_membership.put(irisFisher, new ArrayList<>());
        }
    }

    public void initCentroid(int maxCluster){
        int numbCluster = 0;
        centroid = new HashMap<>();
        for (Map.Entry<IrisFisher, ArrayList<Double>> point : cluster_membership.entrySet()){
            if(numbCluster == maxCluster) break;
            centroid.put(numbCluster, point.getKey());
            ++numbCluster;
        }
    }

    public void initClusters(int k){
        for (Map.Entry<IrisFisher, ArrayList<Double>> element : cluster_membership.entrySet()){
            for (int countCluster = 0; countCluster < k; ++countCluster){
                element.getValue().add(Math.random());
            }
        }
    }
    public void generateCentroid(double m){
        for (Map.Entry<Integer, IrisFisher> centroidCluster : centroid.entrySet()){
            HashMap<String, Double> newCentroid = new HashMap<>();
            double sum_related_points = 0.0d;
            for (Map.Entry<IrisFisher, ArrayList<Double>>  data_point : cluster_membership.entrySet()){
                for (Map.Entry<String, Double> copy_features : data_point.getKey().getFeatures().entrySet()) {
                    if(!newCentroid.containsKey(copy_features.getKey())){
                        newCentroid.put(copy_features.getKey(), 0.0d);
                    }
                    Double sum = Math.pow(data_point.getValue().get(centroidCluster.getKey()), m) * copy_features.getValue();
                    newCentroid.put(copy_features.getKey(), newCentroid.get(copy_features.getKey()) + sum);
                }
                sum_related_points += Math.pow(data_point.getValue().get(centroidCluster.getKey()), m);
            }
            for (Map.Entry<String, Double> element_features : newCentroid.entrySet()){
                newCentroid.put(element_features.getKey(), element_features.getValue()/sum_related_points);
            }
            centroid.put(centroidCluster.getKey(), new IrisFisher(newCentroid));
        }
    }
    public static double euclid_distance(Map<String, Double> points1, Map<String, Double> points2) {
        double sum = 0.0D;
        for (String key : points1.keySet()) {
            Double pointDb1 = points1.get(key);
            Double pointDb2 = points2.get(key);
            if (pointDb1 != null && pointDb2 != null) {
                sum += Math.pow(pointDb1 - pointDb2, 2.0D);
            }
        }
        return Math.sqrt(sum);
    }
    public void recalculating_distribution_matrix(double m){
        for (Map.Entry<IrisFisher, ArrayList<Double>> point : cluster_membership.entrySet()){
            for (int numbCluster = 0; numbCluster < point.getValue().size(); ++ numbCluster){
                double sum = 0.d;
                for (int numbCentroid = 0; numbCentroid < point.getValue().size(); ++numbCentroid){
                    HashMap<String, Double> copy_features_point = point.getKey().getFeatures();
                    sum += Math.pow(
                            (euclid_distance(copy_features_point, centroid.get(numbCluster).getFeatures()))/
                            (euclid_distance(copy_features_point, centroid.get(numbCentroid).getFeatures())),
                            (2/(m-1)));
                }
                cluster_membership.get(point.getKey()).set(numbCluster, 1/sum);
            }
        }
    }
    void showRes(){
        HashMap<Integer, HashMap<String, Integer>> res = new HashMap<>();
        for (Map.Entry<IrisFisher, ArrayList<Double>> res_cluster : cluster_membership.entrySet()){
            double max = -1000.d;
            int numbResCluster = -1;
            for (int numbCluster = 0; numbCluster < res_cluster.getValue().size(); ++numbCluster){
                if(res_cluster.getValue().get(numbCluster) > max){
                    numbResCluster = numbCluster;
                    max = res_cluster.getValue().get(numbCluster);
                }
            }
            if(!res.containsKey(numbResCluster)){
                res.put(numbResCluster, new HashMap<>());
            }
            if(!res.get(numbResCluster).containsKey(res_cluster.getKey().getSpecies())){
                res.get(numbResCluster).put(res_cluster.getKey().getSpecies(), 0);
            }
            res.get(numbResCluster).put(res_cluster.getKey().getSpecies(),
                    res.get(numbResCluster).get(res_cluster.getKey().getSpecies()) + 1);
        }
        for (Map.Entry<Integer, HashMap<String, Integer>> copy_res : res.entrySet()){
            System.out.println("Cluster " + copy_res.getKey() + ":");
            for (Map.Entry<String, Integer> copy_res_cluster  : copy_res.getValue().entrySet()){
                System.out.println("\tName: " + copy_res_cluster.getKey() + " Value: " + copy_res_cluster.getValue());
            }
        }
    }
    HashMap<IrisFisher,  ArrayList<Double>> cloneClusterMembership() {
        HashMap<IrisFisher, ArrayList<Double>> copy_cluster = new HashMap<>();
        for (Map.Entry<IrisFisher, ArrayList<Double>> cluster : cluster_membership.entrySet()) {
            copy_cluster.put(cluster.getKey(), new ArrayList<>());
            for (Double value : cluster.getValue()) {
                copy_cluster.get(cluster.getKey()).add(value);
            }
        }
        return copy_cluster;
    }
    boolean convergence_check(HashMap<IrisFisher,  ArrayList<Double>> old_cluster_membership, double eps){
        for(IrisFisher key : cluster_membership.keySet()){
            for (int numbCluster = 0; numbCluster < cluster_membership.get(key).size(); ++numbCluster) {
                double difference = Math.abs(cluster_membership.get(key).get(numbCluster) -
                        old_cluster_membership.get(key).get(numbCluster));
                if(difference > eps) return false;
            }
        }
        return true;
    }

    public C_Means(String pathDataSet, int k, double eps, double m) throws IOException {
        loadData(pathDataSet);
        initCentroid(k);
        initClusters(k);
        HashMap<IrisFisher, ArrayList<Double>> old_cluster_membership;

        int countAct = 0;
        for (int i = 0; i < 11; ++i) {
            generateCentroid(m);
            old_cluster_membership = cloneClusterMembership();
            recalculating_distribution_matrix(m);
            ++countAct;
            if(convergence_check(old_cluster_membership, eps)) break;
        }
        System.out.println("CountAct: " + countAct);
        showRes();
    }
}
