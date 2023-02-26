# Scheduler

This is a custom Scheduler, "running" processes sorted by highest priority and longest wait time.

This Scheduler was created as part of a tak given to my class by our IT teacher.
As a result, this Scheduler is just a simple Java Swing (yes I know, why do we still use this in 2023) application, that executes a custom input of mock processes. This application will NOT run any real processes!

This application does not save it's state into a JSON file or similar, all data stored in the `Model.java` class will be gone after a program restart.