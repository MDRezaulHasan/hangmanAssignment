@echo off
SetLocal EnableDelayedExpansion

set type=%1

if !type! == client (
java -cp hangman-0.0.1.jar;jackson-core-2.5.3.jar;jackson-annotations-2.5.0.jar;jackson-databind-2.5.3.jar;jjwt-api-0.10.7.jar;jjwt-impl-0.10.7.jar;jjwt-jackson-0.10.7.jar se.hangman.client.Main 
)
if !type! == server (
java -cp hangman-0.0.1.jar;jackson-core-2.5.3.jar;jackson-annotations-2.5.0.jar;jackson-databind-2.5.3.jar;jjwt-api-0.10.7.jar;jjwt-impl-0.10.7.jar;jjwt-jackson-0.10.7.jar se.hangman.server.Main
)
else (
echo "Argument passed is either wrong or empty"
echo "Please pass either cerver or client
)

	


