package ru.it4u24.joker;

import java.util.ArrayList;

public class InitList {

    private String[][] args;

    public InitList(String[][] data) {
        this.args = data;
    }

    public ArrayList<Rc> getRcArrayList() {
        return getRcArrayListFromDataBase(args);
    }

    public ArrayList<ElectronicQueue> getEqArrayList() {
        return getEqArrayListFromDataBase(args);
    }

    private ArrayList<Rc> getRcArrayListFromDataBase(String[][] args) {
        ArrayList<Rc> arrayList = new ArrayList<>();

        arrayList.add(new Rc("Выберите автоцентр","",0,0,0));

        for (int i = 0; i < args.length; i++) {
            int id = Integer.parseInt(args[i][1]);
            int worktime = Integer.parseInt(args[i][3]);
            int minut = Integer.parseInt(args[i][4]);
            arrayList.add(new Rc(args[i][2], args[i][0], id, worktime, minut));
        }

        return arrayList;
    }

    private ArrayList<ElectronicQueue> getEqArrayListFromDataBase(String[][] args) {
        ArrayList<ElectronicQueue> arrayList = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            arrayList.add(new ElectronicQueue(args[i][0], args[i][3]));
        }

        return arrayList;
    }

}
