package edaii.simcovid.game;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CovidLogic {

    private final int STATE_NOT_INFECTED = 0;
    private final int STATE_INFECTED = 1;
    private final int STATE_IMMUNE = 2;
    private final int STATE_SURROUNDED = 3;
    private final int STATE_MASKED = 4;
    private final int STATE_DEAD = 5;
    private final int STATE_MASKED_INFECTED = 6;

    private final VirusParameters parameters;

    public CovidLogic(VirusParameters parameters) {

        this.parameters = parameters;
    }

    public List<List<Person>> advanceDay(List<List<Person>> population, final int rows, int columns) {

        return Stream.iterate(0, x -> x < rows, x -> x + 1)
                .map(x -> Stream.iterate(0, y -> y < columns, y -> y + 1)
                        .map(y -> getStateOfPerson( x, y, population))
                        .collect(Collectors.toUnmodifiableList()))
                .collect(Collectors.toUnmodifiableList());
    }

    private Person getStateOfPerson(Integer x, Integer y, List<List<Person>> population) {

        final Person person = population.get(x).get(y);

        if(person.getState()==STATE_DEAD) return person.setState(STATE_DEAD);

        if(person.getState()==STATE_IMMUNE) return person.setState(STATE_IMMUNE);

        if(person.getIsInfected()==STATE_INFECTED){

            if(person.getDaysInfected()==parameters.lifetimeInDays) return person.setState(STATE_IMMUNE)
                    .setIsInfected(2).setDaysInfected(0);

            if(getRandomNumber()<= parameters.killPercent) return person.setState(STATE_DEAD).setIsInfected(2)
                    .setDaysInfected(0);

            if(getRandomNumber()<= parameters.wearMaskPercent) return person.setState(STATE_MASKED_INFECTED)
                    .setDaysInfected(person.getDaysInfected()+1);

            return person.setState(STATE_INFECTED).setDaysInfected(person.getDaysInfected()+1);
        }

        //Obtengo personas vecinas a persona base
        final List<List<Person>> adjacentPersons = getAdjacentPersons(x, y, population);

        //Obtengo las personas infectadas de las personas vecinas
        final List<Person> infectedPeople = getInfectedPeople(adjacentPersons);

        //Obtengo las veces que la persona es infectada, si una persona la infecta sumo 1
        final int timesPersonIsInfected = getTimesPersonIsInfected(person, infectedPeople);

        if(timesPersonIsInfected>0){
            if (getRandomNumber()<= parameters.wearMaskPercent) return person.setIsInfected(1)
                    .setState(STATE_MASKED_INFECTED);
            else return person.setIsInfected(1).setState(STATE_INFECTED);
        }

        if(getRandomNumber()<= parameters.wearMaskPercent) return person.setState(STATE_MASKED);

        if(infectedPeople.size()>= parameters.personSurrounded) return person.setState(STATE_SURROUNDED);

        return person.setState(STATE_NOT_INFECTED);
    }

    private List<List<Person>> getAdjacentPersons(Integer i, Integer j, List<List<Person>> population) {

        final int range = parameters.transmissionRange;
        final int rows = population.size();
        final int columns = population.get(0).size();

        return Stream.iterate(i-range, x-> x<=i+ range, x-> x+1)
                .filter(x->j<columns)
                .filter(x->i<rows)
                .map(x-> Stream.iterate(j-range, y-> y<=j+range, y->y+1)
                        .filter(b -> x!=i || b!= j )
                        .filter(a-> x>=0 && x<rows)
                        .filter(y-> y>=0 && y<columns)
                        .map(y-> population.get(x).get(y).setDistance(getDistance(i, j, x, y)) )
                        .collect(Collectors.toUnmodifiableList()))
                .collect(Collectors.toUnmodifiableList());
    }

    private int getDistance(int fileOriginal, int columnOriginal, int file, int column) {

        final int x = (int) Math.pow(fileOriginal-file, 2);
        final  int y = (int) Math.pow(columnOriginal-column, 2);

        return (int) Math.sqrt(x+y);
    }

    private List<Person> getInfectedPeople(List<List<Person>> adjacentPersons) {

        final List<List<Person>> infectedPeople = Stream.iterate(0, i-> i< adjacentPersons.size(), i-> i+1)
                .map(i-> Stream.iterate(0, j-> j< adjacentPersons.get(i).size(), j-> j+1)
                        .filter(j-> adjacentPersons.get(i).get(j).getIsInfected()==1)
                        .map(j-> adjacentPersons.get(i).get(j))
                        .collect(Collectors.toUnmodifiableList())
                ).collect(Collectors.toUnmodifiableList());

        return infectedPeople.stream().flatMap(List::stream).collect(Collectors.toList());
    }

    private int getTimesPersonIsInfected(Person person, List<Person> infectedPeople) {

        return Stream.iterate(0, s-> s< infectedPeople.size(), s->s+1)
                .filter(a-> canBeInfected(infectedPeople.get(a), person) == true)
                .mapToInt(a->1).sum();
    }

    private boolean canBeInfected(Person personInfected, Person personOriginal) {

        final int transmissionPercent = parameters.transmissionPercent/personInfected.getDistance();

        if(personInfected.getState()==STATE_MASKED_INFECTED && personOriginal.getState()==STATE_MASKED){
            if(getRandomNumber()<=transmissionPercent/4) return true;
            else return false;
        }

        if(personInfected.getState()==STATE_MASKED_INFECTED || personOriginal.getState()==STATE_MASKED){
            if(getRandomNumber()<=transmissionPercent/2) return true;
            else return false;
        }

        if(getRandomNumber()<= transmissionPercent){
            return true;
        }

        return false;
    }

    private int getRandomNumber(){
        return (int) (Math.random()*100);
    }
}
