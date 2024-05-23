package edaii.simcovid.game;

public class Person {

    private final int state;
    private final int daysInfected;
    private final int isInfected;
    private final int distance;

    /**
     *
     * @param state Estado de la persona
     * @param daysInfected Dias que lleva una persona infectada
     * @param isInfected Saber si esta infectado o no, 0-No infectado, 1-Infectado, 2-Nada
     * @param distance Distancia de una persona respecto a otra
     */

    public Person(int state, int daysInfected, int isInfected, int distance){
        this.state=state;
        this.daysInfected=daysInfected;
        this.isInfected=isInfected;
        this.distance=distance;
    }

    public Person setDistance(int value){
        return new Person(getState(), getDaysInfected(), getIsInfected(), value);
    }

    public Person setState(int state){ return new Person(state, getDaysInfected(), getIsInfected(), getDistance());}

    public Person setDaysInfected(int daysInfected){return new Person(getState(), daysInfected, getIsInfected(), getDistance());}

    public Person setIsInfected(int isInfected){return new Person(getState(), getDaysInfected(),isInfected, getDistance());}

    public int getState() {return state; }

    public int getDaysInfected(){return daysInfected;}

    public int getIsInfected(){return isInfected;}

    public int getDistance(){return distance;}

}