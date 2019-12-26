package ru.it4u24.joker;

import java.util.ArrayList;

public class RcList {

    ArrayList<Rc> rcArrayList;

    public RcList() {
        this.rcArrayList = getRcArrayListFromDataBase();
    }

    public ArrayList<Rc> getRcArrayList() {
        return rcArrayList;
    }

    private ArrayList<Rc> getRcArrayListFromDataBase() {
        ArrayList<Rc> rcArrayListFromDataBase = new ArrayList<>();
        rcArrayListFromDataBase.add(new Rc("Выберите автоцентр",            0,0,0));
        rcArrayListFromDataBase.add(new Rc("ОП РЦ ТФК в г.Набережные Челны",1,8,20));
        rcArrayListFromDataBase.add(new Rc("ОП РЦ ТФК в г.Вологда",         2,8,20));
        rcArrayListFromDataBase.add(new Rc("ОП РЦ ТФК в г.Йошкар-Ола",      3,8,20));
        rcArrayListFromDataBase.add(new Rc("ОП РЦ ТФК в г.Казань",          4,12,20));
        rcArrayListFromDataBase.add(new Rc("ОП РЦ ТФК в г.Кемерово",        5,8,20));
        rcArrayListFromDataBase.add(new Rc("ОП РЦ ТФК в г.Липецк",          6,8,20));
        rcArrayListFromDataBase.add(new Rc("ОП РЦ ТФК в г.Нижний Новгород", 7,12,20));
        rcArrayListFromDataBase.add(new Rc("ОП РЦ ТФК в г.Ростов-на-Дону",  8,8,20));
        rcArrayListFromDataBase.add(new Rc("ОП РЦ ТФК в г.Ставрополь",      9,8,20));
        rcArrayListFromDataBase.add(new Rc("ОП РЦ ТФК в г.Хабаровск",      10,8,20));
        return rcArrayListFromDataBase;
    }
}
