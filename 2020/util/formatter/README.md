# Installing the format hook #

If you would like to ensure the build CI does not reject your code due to a formatting issue, please
install the pre-commit hook.

From the top level repo:

    $ cp util/formatter/pre-commit .git/hooks/

Now, whenever you commit, git will take a second to run the google-java-formatter for you.
