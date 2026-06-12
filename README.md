# EcoSystemSimulator for Java, TPII, UCM
Made by Andrés Rodríguez García & Rodrigo Álvarez Hernández
==================================================================================================================================================================================================================
Necessary:
Java 21
Maven
How to run the project?
1) Clone the repository.
2) Open the cmd in the root of the project (where the src, lib and pom are located).
3) Use: mvn clean package.
4) Use: java -jar target/ecosystem-simulator-1.1.0-SNAPSHOT-jar-with-dependencies.jar
5) Enjoy :)

This project implements an ecosystem simulator that models the interactions between carnivorous and herbivorous animals in a dynamic environment. The main objective is to apply object-oriented design principles, utilizing generics and collections in Java to create a complex and realistic system.

The simulator reproduces a two-dimensional ecosystem where wolves (carnivores) and sheep (herbivores) coexist, each with autonomous behaviors determined by their internal state and perceived environment. Animals can find themselves in different states: normal, hungry, looking for a mate, fleeing danger, or dead. These transitions depend on factors such as available energy, reproductive drive, age, and the presence of other animals within their field of view.

The simulation world is organized into a matrix of regions that function as food sources for the herbivores. Animals consume energy when moving and performing activities, and they must feed to survive. When two compatible animals meet, they can reproduce, generating offspring that inherit traits from their parents with minor mutations, thus enabling the evolution of the ecosystem.

The simulation uses a discrete-time system where each step updates the state of all animals and regions. The system allows configuring the initial state via JSON files. Simulation results can be visualized in real-time and exported in JSON format for later analysis.

This project constitutes the practical assignment for the Programming Technology II course at the Complutense University of Madrid for the 2025/2026 academic year.
