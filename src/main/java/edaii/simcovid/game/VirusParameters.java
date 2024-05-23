package edaii.simcovid.game;

public class VirusParameters {
    final int transmissionPercent;
    final int lifetimeInDays;
    final int killPercent;
    final int transmissionRange;
    final int personSurrounded;
    final int wearMaskPercent;

    /**
     * @param transmissionPercent Porcentaje de transmisión del virus
     * @param lifetimeInDays Duración de vida del virus
     * @param killPercent Porcentaje de morir si estás infectado
     * @param transmissionRange Rango de transmisión del virus
     * @param personSurrounded Mínimo de personas para sentirse rodeado
     * @param wearMaskPercent Porcentaje de llevar mascarilla
     */

    public VirusParameters( int transmissionPercent, int lifetimeInDays, int killPercent, int transmissionRange,
                            int personSurrounded, int wearMaskPercent) {
        this.transmissionPercent = transmissionPercent;
        this.lifetimeInDays = lifetimeInDays;
        this.killPercent=killPercent;
        this.transmissionRange=transmissionRange;
        this.personSurrounded=personSurrounded;
        this.wearMaskPercent=wearMaskPercent;
    }

}
