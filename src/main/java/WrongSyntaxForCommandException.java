public class WrongSyntaxForCommandException extends Exception {
    public WrongSyntaxForCommandException(Command command) {
        super("Sumo understood your command but dunno what you want! Please utilise \""
                + command
                + "\" the correct way.");
    }
}
