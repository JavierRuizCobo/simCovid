package edaii.simcovid.app;
import edaii.simcovid.game.CovidLogic;
import edaii.simcovid.game.Person;
import edaii.simcovid.game.VirusParameters;
import edaii.simcovid.ui.CovidGameWindow;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CovidGame {

    public static final int ROWS = 10;
    public static final int COLUMNS = 10;
    public static final int MSECONDS_PER_DAY = 1000;
    public static final int VIRUS_TRANSMISSION_PERCENT = 8;
    public static final int VIRUS_KILL_PERCENT = 1;
    public static final int TRANSMISSION_RANGE = 1;
    public static final int VIRUS_TIMELIFE_DAYS = 14;
    public static final int PERSON_IS_SURROUNDED = 4;
    public static final int WEAR_MASK_PERCENT = 50;


    public static void main(String[] args) throws InterruptedException {

        final CovidGameWindow game = new CovidGameWindow();
        game.setRowsAndColumns(ROWS, COLUMNS);

        final VirusParameters virusParameters = new VirusParameters(
                VIRUS_TRANSMISSION_PERCENT,
                VIRUS_TIMELIFE_DAYS,
                VIRUS_KILL_PERCENT,
                TRANSMISSION_RANGE,
                PERSON_IS_SURROUNDED,
                WEAR_MASK_PERCENT
        );

        final CovidLogic covidLogic = new CovidLogic(virusParameters);

        // Inicializa la población
        List<List<Person>> population = initializePopulation(ROWS, COLUMNS);

        while (true) {

            final int totInfectedYesterday = getCountIsInfected(population,1);
            final int totRecoveredYesterday = getCountOfState(population, 2);
            final int totDeathsYesterday =  getCountOfState(population, 5);

            // Avanza el día
            population = covidLogic.advanceDay(population, ROWS, COLUMNS);

            final int totInfectedToday = getCountIsInfected(population,1);
            final int totNotInfectedToday = getCountIsInfected(population,0);
            final int totRecoveredToday = getCountOfState(population, 2);
            final int toDeathsToday =  getCountOfState(population, 5);

            final int deaths= toDeathsToday-totDeathsYesterday;
            final int recovered=totRecoveredToday-totRecoveredYesterday;
            final int infected = (totInfectedToday+deaths+recovered) - totInfectedYesterday;

            printInform(infected, recovered, deaths);

            // Convierte la población en una lista de enteros
            // representando estados validos para edaii.simcovid.ui.Cell

            final List<Integer> cellStates = population
                    .stream()
                    .flatMap(row -> row.stream())
                    .map(it -> it.getState())
                    .collect(Collectors.toUnmodifiableList());

            // Representa el estado
            game.setCellStates(cellStates);

            final int sum = toDeathsToday+totRecoveredToday+totNotInfectedToday;

            if(sum == COLUMNS*ROWS) break;

            // Pasa el día
            Thread.sleep(MSECONDS_PER_DAY);
        }

        System.out.println("Finalizada la simulación");
        System.out.println("-------------------------------");
        System.out.println("Muertos totales: " + getCountOfState(population, 5));
        System.out.println("Inmunes: " +  getCountOfState(population, 2));
        System.out.println("Dioses: " + getCountIsInfected(population,0));
    }

    private static List<List<Person>> initializePopulation(int rows, int columns) {
        return Stream.iterate(0, i -> i < rows, i -> i + 1)
                .map(i -> Stream.iterate(0, j -> j < columns, j -> j + 1)
                        .map(j -> j == columns / 2 && i == rows / 2)
                        .map(infected -> infected ?
                                new Person(1,0, 1, 0) :
                                getRandomNumber()<=WEAR_MASK_PERCENT ?
                                        new Person(4,0, 0,0)
                                        :
                                        new Person(0, 0, 0,0)
                        ).collect(Collectors.toUnmodifiableList())
                ).collect(Collectors.toUnmodifiableList());
    }

    private static int getCountOfState(List<List<Person>> population, int state) {
        final List<Integer> infected = population
                .stream()
                .flatMap(row -> row.stream())
                .map(it -> it.getState())
                .collect(Collectors.toUnmodifiableList());

        return infected.stream()
                .filter(elem-> elem==state)
                .mapToInt(elem -> 1)
                .sum();
    }

    private static int getCountIsInfected(List<List<Person>> population, int isInfected) {

        final List<Integer> infected = population
                .stream()
                .flatMap(row -> row.stream())
                .map(it -> it.getIsInfected())
                .collect(Collectors.toUnmodifiableList());

        return infected.stream()
                .filter(elem-> elem==isInfected)
                .mapToInt(elem -> 1)
                .sum();
    }

    private static void printInform(int infected, int recovered, int deaths) {

        System.out.println("---------------------------------------");
        System.out.println("INFORME DIARIO");
        System.out.println("Nuevos contagios: " + infected);
        System.out.println("Recuperados: "+ recovered);
        System.out.println("Muertos: "+ deaths);
        System.out.println("---------------------------------------");
    }

    private static int getRandomNumber(){
        return (int) (Math.random()*100);
    }
}