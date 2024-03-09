package ru.hse.vectorizer.utils;

public final class Constants {
    public static final String GREETING = """
                                 __               .__                    \s
            ___  __ ____   _____/  |_  ___________|__|_______ ___________\s
            \\  \\/ // __ \\_/ ___\\   __\\/  _ \\_  __ \\  \\___   // __ \\_  __ \\
             \\   /\\  ___/\\  \\___|  | (  <_> )  | \\/  |/    /\\  ___/|  | \\/
              \\_/  \\___  >\\___  >__|  \\____/|__|  |__/_____ \\\\___  >__|  \s
                       \\/     \\/                           \\/    \\/      \s
            """;
    public static final String HELP = """
           This program is supposed to be used for some operations on vectors such as storing, loading, adding, etc.
           Available commands is:
           exit                             - stops the program
            """;

    public static final String CLIENT_HELP = """
           This program is supposed to be used for some operations on vectors such as storing, loading, adding, etc.
           Available commands are:
           
           help                             - shows this message
           create $name $x $y $z            - creates a vector with specified name and coords
           read                             - shows all created (or currently loaded) vectors
           range $name                      - shows a length of a vector with specified name
           angle $first $second             - calculates an angle between two vectors
           product $prodType $first $second - calculates product of two vectors where $prodType can be
                                              dot or cross
           product triple $a $b $c          - get a*b*c where a, b, c - vectors, and * is cross product
           read $pageSize $pageNumber       - outputs vectors of specified page
           delete $name                     - deletes vector with specified name
           exit                             - stops the program
           
            """;

    public static final String FIND_BY_NAME = "SELECT * FROM vector WHERE name = ? AND login = ?";
    public static final String DELETE_BY_NAME = "DELETE FROM vector WHERE name = ? AND login = ?";
    public static final String COUNT_ROWS = "SELECT COUNT(*) FROM vector WHERE login=?";
    public static final String SELECT_ALL = "SELECT * FROM vector WHERE login=?";
    public static final String GET_PAGE = """
                SELECT * FROM vector WHERE login=?
                ORDER BY updated_at
                OFFSET ? ROWS FETCH NEXT ? ROWS ONLY""";

    public static final String UPDATE = "UPDATE vector SET x=?, y=?, z=?, updated_at=CURRENT_TIMESTAMP WHERE name=? AND login=?";

    public static final String CREATE = "INSERT INTO vector (name, x, y, z, login) VALUES (?, ?, ?, ?, ?);";

    public static final String LOGIN_SYMBOLS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static final String WRONG_LOGIN = "Please reconnect with correct login. " +
            "You should use only letters and/or numbers, length of your login must be between 3 and 15 symbols";

    public static final String CORRECT_LOGIN = "You have entered with your login." +
            "Now you can write help to get a list of commands";

    private Constants() {}
}
