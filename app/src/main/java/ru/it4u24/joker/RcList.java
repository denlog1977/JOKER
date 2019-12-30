package ru.it4u24.joker;

import java.util.ArrayList;
import java.util.Map;

public class RcList {

    ArrayList<Rc> rcArrayList;

    public RcList(String[][] args) {
        this.rcArrayList = getRcArrayListFromDataBase(args);
    }

    public ArrayList<Rc> getRcArrayList() {
        return rcArrayList;
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
}
