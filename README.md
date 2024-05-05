# CMPS405_Project
## Team
- Salem Al-Ansari sa2001626@qu.edu.qa
- Mhd Hadi Nouh   mn1909591@qu.edu.qa
- Ahmed Alhato    aa1805179@qu.edu.qa
- Mustafa Elqaq   me1808529@qu.edu.qa

## Challenges
1. Adding a way for the player to send and receive to and from the server, this was an issue as the player needs to be able to send to the server when he needs to select a number, join a room, and so on. Solved by keeping the sending and receiving on seperate threads.
2. Incorporating the chat, players need to have the ability to chat with each other only within the game room. The solution was to add a function to both the player class and the game class, where the game can broadcast the messages to all the players, and the players are able to receive the messages.
3. Adding the condition where if there are only two players, the player that chooses the value 0 always loses. Solved by adding a check to see the number of players, and whether the last player's selection was a 0, if not then the first player had the 0 and the game would decrease a point and count him as the loser.
4. Incorporating a way to eliminate players, but keep the ability to spectate the game. The solution was the create a seperate Arraylist which adds the eliminated players, and then checks whether the player has been eliminated to stop the player from selecting a number.
5. Adding a way to start the game when the player number has reached the minimum. Fixed by adding a vote to start, where all the players have to vote so that the game would start.
6. Differentiating between whether the user wants to chat or type a command. The solution was to add a switch case, where it would check if the user has typed a command using a slash "/", if not then this would mean that they want to chat, and there is also a check to see if there is a game active or not.
7. Implementing the leaderboard. To implement the leaderboard we used a hash map, where it takes a string and an integer, when the user gets a new "GamesWon" score, meaning increasing the number of games won score for the player variable, the leaderboard would update through using the put function that is available in the hash map. Moreover, to print out the best 5 playes, the has map has a stream() function, which returns a sequential Stream over the elements in the collection, sorting it in a reverse fashion to get the larger score in the beginning, and then printing the first 5.

## Contributions
| Member | Date | Description |
| :----- | ---------: | :---------- |
| Salem | 2024-04-24 | Initial commit|
| Salem | 2024-04-24 | Project Files|
| Salem | 2024-04-26 | Added a chat|
| Salem | 2024-05-03 | Added vote to start the game|
| Salem | 2024-05-05 | Added elimination|
| Salem | 2024-05-05 | Optimization|
| Salem | 2024-05-05 | Added a leaderboard and removed unused files|
