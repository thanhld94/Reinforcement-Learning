# A New Reinforcement Learning Algorithm for a Game-Playing Agent
Reinforcement learning is one of the best learning algorithm that resemble human learning experience when but into unknown contexts very well. It has algorithms that are powerful tools to help computers understand and learn completely unfamiliar environmfents, but many of the algorithms in common use have weaknesses.  One key weakness is the inability to learn when the number of states used to represent the environment is large. Our research addresses this weakness by combining a reinforcement learning approach called Q-learning with another type of machine learning algorithm called Adaptive Resonance Theory (ART), a neural network algorithm in unsupervised learning, to comprehend more about artificial intelligences and to create an agent that can handle the weaknesses we have using traditional method and can learn and adapt to different environments.  ART clusters the states into groups represent similar states, so it helps Q-learning resolve the space difficulty, and it also helps the reinforcement agent learn quicker by recognizing the similar states. By creating a new algorithm fusing Q-learning and ART we are able to demonstrate a more intelligent agent than the traditional methods allow. Our results are illustrated using the games Pac-Man and Bomber-man where we show our algorithm results in a higher rate of success than does the standard approach.

## Cliffwalking
- The basic step of learning Reinforcement Learning (Q-Learning).

## MsPacman
- Original reinforcement learning project.
- The agent uses only Q-Learning to learn and play the game.

## rlArt (Reinforcement Learning - ART)
- The project uses Q-Learning combining with Adaptive-Resonance-Theory to learn a more complicated game (Bomberman).
- The goal of the game is to destroy as many distroyable blocks as possible (exploration) without being catched by the enemies. 
- ART is used to recognize similar states of the game.
