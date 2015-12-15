package com.fssqawj.sensim;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import wordsimilarity.WordSimilarity;

import java.util.*;

/**
 * Created by fssqa on 2015/12/7.
 */
public class Sensim {
    public static double wordSimVal(String wordx, String wordy){
        if(wordx.contains(wordy))return 10;
        return WordSimilarity.simWord(wordx, wordy);
    }
    public static double senSimVal(String senx,String seny){
        List<String> senxTerm = getTerm(senx);
        List<String> senyTerm = getTerm(seny);
        int lenx = senxTerm.size();
        int leny = senyTerm.size();
        double res = 0;
        for(String iKey : senxTerm){
            for(String jKey : senyTerm){
                res += wordSimVal(iKey, jKey);
            }
        }
        return res / (lenx * leny);
    }

    public static double sensVecSim(String senx, String seny){
        List<String> senxTerm = getTerm(senx);
        List<String> senyTerm = getTerm(seny);
        int lenx = senxTerm.size();
        int leny = senyTerm.size();
        double res = 0;
        for(String iKey : senxTerm){
            for(String jKey : senyTerm){
                if(iKey.contains(jKey))res += 1;
            }
        }
        return res / Math.sqrt(lenx * leny);
    }

    public static List<String> getTerm(String str){
        //String test = "我在实验室里做作业，好无聊啊~";
        List<String> res = new ArrayList<String>();
        List<Term> iTerm = ToAnalysis.parse(str);
        for(Term key : iTerm){
            String tem = key.toString();
            String[] ary = tem.split("/");
            if(ary.length > 0)res.add(ary[0]);
        }
        return res;
    }

    public static void main(String[] args){
        //String test = "我在实验室里做作业，好无聊啊~";
        //System.out.println(getTerm(test));
        //Question[] questions = new Question[13941];
        List<Question> hList = new ArrayList<Question>();
        List<Question> rList = new ArrayList<Question>();
        Map<String, Integer> hMap = new HashMap<String, Integer>();

        Map<String, Integer> rMap = new HashMap<String, Integer>();

        Map<Integer, List<Integer>> matchQuestion = new HashMap<Integer, List<Integer>>();

        Set<String> iSet = new HashSet<String>();

        ReadFile reader = new ReadFile("corpus.txt");

        //WriteFile writer = new WriteFile("test.txt");

        String temp = null;
        int hcnt = 1;
        int rcnt = 1;
        int cnt = 1;
        int tcnt = 1;
        while((temp = reader.readLine()) != null){
            String[] qtem = temp.split("\t####\t");
            System.out.println(temp);
            String hq = qtem[0];
            String rq = qtem[1];
            //questions[cnt] = new Question();
            if(!hMap.containsKey(hq)){
                hMap.put(hq, hcnt);
                hcnt = hcnt + 1;
                Question tq = new Question();
                tq.setContent(hq);
                hList.add(tq);
            }
            if(!rMap.containsKey(rq)){
                rMap.put(rq, rcnt);
                rcnt = rcnt + 1;
                Question tq = new Question();
                tq.setContent(rq);
                rList.add(tq);
            }

            int hid = hMap.get(hq);
            int rid = rMap.get(rq);

            if(!matchQuestion.containsKey(hid)){
                List<Integer> tem = new ArrayList<Integer>();
                tem.add(rid);
                matchQuestion.put(hid, tem);
            }
            else {
                List<Integer> tem = matchQuestion.get(hid);
                tem.add(rid);
                matchQuestion.put(hid, tem);
            }

            System.out.println(cnt ++);
            if(cnt % 50 == 47){
                iSet.add(hq);
                tcnt = tcnt + 1;
            }
        }

        reader.close();
        System.out.println("tcnt : " + tcnt);
        int hit = 0;
        for(int i = 0;i < hList.size();i ++){
            String hq = hList.get(i).getContent();
            if(!iSet.contains(hq))continue;
            for(int j = 0;j < rList.size();j ++){
                String rq = rList.get(j).getContent();
                rList.get(j).setSrc(sensVecSim(hq, rq));
            }
            System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
            Collections.sort(rList);
            int hid = hMap.get(hq);
            System.out.println(hq);
            for(int k = 0;k < 10;k ++){
                System.out.println(k+"-------\n" + rList.get(k).getContent());
                //int t = rMap.get("什么是沪港通");
                int id = rMap.get(rList.get(k).getContent());
                if(matchQuestion.get(hid).contains(id)){
                    hit ++;
                    break;
                }
            }


        }

        System.out.println("hit : " + hit);
    }

}
