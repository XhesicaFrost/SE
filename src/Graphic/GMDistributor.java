package Graphic;

public class GMDistributor {
    public static GraphicalManager getGM(String stateName)
    {
        return switch (stateName) {
            case "A" -> new GraphicalManager() {
            };
            case "B" -> new GraphicalManager() {
            };
            default -> null;
        };
    }
}
