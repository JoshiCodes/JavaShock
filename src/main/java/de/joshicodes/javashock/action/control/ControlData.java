package de.joshicodes.javashock.action.control;

public record ControlData(ControlType type, int intensity, long duration) {

    public enum ControlType {

        SHOCK,
        VIBRATE,
        SOUND,
        STOP;

        public String getName() {
            return this.name().charAt(0) + this.name().substring(1).toLowerCase();
        }

    }

}
