# Komunikator z szyfrowaniem

## Opis aplikacji

Komunikator szyfrujący wiadomości, implementujący protokół Diffiego-Hellmana oraz obsługujący format komunikacji JSON. 
Wiadomości zostają zakodowane przed wysłaniem ich za pomocą BASE64. Komunikator działa w architekturze client-server, możliwość 
obsługi wielu klientów.

## Sposób uruchomienia
kompilacja:
- javac -cp json-20190722.jar Server.java
- javac -cp json-20190722.jar Client.java

uruchomienie:
- java -cp .;json-20190722.jar Server 4040 // jako argument numer portu
- java -cp .;json-20190722.jar Client localhost 4040 Marek // uruchomienie jednego klienta, argumenty - host, port, imię

## Wymaganie systemowe

Java w wersji > 1.8
