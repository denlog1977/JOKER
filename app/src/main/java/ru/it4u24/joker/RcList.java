package ru.it4u24.joker;

import java.util.ArrayList;

public class RcList {

    ArrayList<Rc> rcArrayList;
    ArrayList<ElectronicQueue> eqArrayList;

    public RcList(String[][] args) {
        this.rcArrayList = getRcArrayListFromDataBase(args);
    }

    public RcList(Class T, String[][] args) {
        this.eqArrayList = getEqArrayListFromDataBase(args);
    }

    public ArrayList<Rc> getRcArrayList() {
        return rcArrayList;
    }

    public ArrayList<ElectronicQueue> getEqArrayList() {
        return eqArrayList;
    }

    private ArrayList<Rc> getRcArrayListFromDataBase(String[][] args) {
        ArrayList<Rc> rcArrayListFromDataBase = new ArrayList<>();

        rcArrayListFromDataBase.add(new Rc("Выберите автоцентр", 0,0,0));
        /*rcArrayListFromDataBase.add(new Rc("ОП РЦ ТФК в г.Набережные Челны",1,8,20));
        rcArrayListFromDataBase.add(new Rc("ОП РЦ ТФК в г.Вологда",         2,8,20));
        rcArrayListFromDataBase.add(new Rc("ОП РЦ ТФК в г.Йошкар-Ола",      3,8,20));
        rcArrayListFromDataBase.add(new Rc("ОП РЦ ТФК в г.Казань",          4,12,20));
        rcArrayListFromDataBase.add(new Rc("ОП РЦ ТФК в г.Кемерово",        5,8,20));
        rcArrayListFromDataBase.add(new Rc("ОП РЦ ТФК в г.Липецк",          6,8,20));
        rcArrayListFromDataBase.add(new Rc("ОП РЦ ТФК в г.Нижний Новгород", 7,12,20));
        rcArrayListFromDataBase.add(new Rc("ОП РЦ ТФК в г.Ростов-на-Дону",  8,8,20));
        rcArrayListFromDataBase.add(new Rc("ОП РЦ ТФК в г.Ставрополь",      9,8,20));
        rcArrayListFromDataBase.add(new Rc("ОП РЦ ТФК в г.Хабаровск",      10,8,20));*/


        for (int i = 0; i < args.length; i++) {
            int id = Integer.parseInt(args[i][1]);
            int worktime = Integer.parseInt(args[i][3]);
            int minut = Integer.parseInt(args[i][4]);
            rcArrayListFromDataBase.add(new Rc(args[i][2], id, worktime, minut));
        }

        return rcArrayListFromDataBase;
    }

    private ArrayList<ElectronicQueue> getEqArrayListFromDataBase(String[][] args) {
        ArrayList<ElectronicQueue> arrayListFromDataBase = new ArrayList<>();

        //rcArrayListFromDataBase.add(new ElectronicQueue("Выберите автоцентр"));

        for (int i = 0; i < args.length; i++) {
            //int id = Integer.parseInt(args[i][1]);
            //int worktime = Integer.parseInt(args[i][3]);
            //int minut = Integer.parseInt(args[i][4]);
            //String name =  + " - " + args[i][3];
            arrayListFromDataBase.add(new ElectronicQueue(args[i][0], args[i][3]));
        }

        return arrayListFromDataBase;
    }

}
