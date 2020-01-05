package ru.it4u24.joker;

import java.util.ArrayList;

public class InitList {

    //ArrayList<Rc> rcArrayList;
    //ArrayList<ElectronicQueue> eqArrayList;
    ArrayList<?> arrayList;

    public InitList(Class T, String[][] args) {
        if (T == Rc.class) {
            //this.rcArrayList = getRcArrayListFromDataBase(args);
            this.arrayList = getRcArrayListFromDataBase(args);
        } else if (T == ElectronicQueue.class) {
            //this.eqArrayList = getEqArrayListFromDataBase(args);
            this.arrayList = getEqArrayListFromDataBase(args);
        }
    }

    /*public ArrayList<Rc> getRcArrayList() {
        return rcArrayList;
    }*/

    /*public ArrayList<ElectronicQueue> getEqArrayList() {
        return eqArrayList;
    }*/

    public ArrayList<?> getArrayList() {
        return arrayList;
    }

    private ArrayList<Rc> getRcArrayListFromDataBase(String[][] args) {
        ArrayList<Rc> rcArrayListFromDataBase = new ArrayList<>();

        rcArrayListFromDataBase.add(new Rc("Выберите автоцентр", 0,0,0));

        for (int i = 0; i < args.length; i++) {
            int id = Integer.parseInt(args[i][1]);
            int worktime = Integer.parseInt(args[i][3]);
            int minut = Integer.parseInt(args[i][4]);
            rcArrayListFromDataBase.add(new Rc(args[i][2], id, worktime, minut));
        }

        return rcArrayListFromDataBase;
    }

    private ArrayList<ElectronicQueue> getEqArrayListFromDataBase(String[][] args) {
        ArrayList<ElectronicQueue> arrayList = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            arrayList.add(new ElectronicQueue(args[i][0], args[i][3]));
        }

        return arrayList;
    }

}