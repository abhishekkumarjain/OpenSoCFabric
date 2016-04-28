// Provide a managed dependency on chisel if -DchiselVersion="" is
// supplied on the command line.

val chiselVersion_u = System.getProperty("chiselVersion", "3.0")

// _u a temporary fix until sbt 13.6 https://github.com/sbt/sbt/issues/1465

libraryDependencies ++= ( if (chiselVersion_u != "None" ) ("edu.berkeley.cs" %% "chisel3" % chiselVersion_u) :: Nil; else Nil)
