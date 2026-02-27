# FFOnline

FFOnline is a Java reimplementation of the original NES release of Final Fantasy 1, inspired by Multi-User Dungeons (MUDs). It is a learning project for me to explore online game server programming.

FFOnline is being designed to be reasonably true to FF1, within the constraints of the conversion to a text-based game. Because of that, it plays quite different from a regular MUD, since each player controls a 4-character party and player interaction is very limited.

## Todo
 - ~~Simple TCP server that accepts connections~~
 - ~~Global chat~~
 - ~~Items and equipment~~
 - Player character and progression
 - Magic and enemies' special attacks
 - Magic and items out of battle
 - Player party and character creation
 - Encode enemy data and AI
 - Battle commands (FIGHT, MAGIC, DRINK, etc.)
 - Enemy formations
 - Battle system
 - Locations (overworld, towns, shops, dungeons)
 - Local chat and whispers
 - Key items and game progress

## Limitations

 - Plain text only; support for encryption, compression, or MUD-specific protocols (e.g. GMCP, MSSP, etc.) is not planned
 - No persistent data is stored, your account and game progress are lost as soon as you disconnect; data storage in a database is not a planned feature

## How to Build

No idea :(

## Credits
Code by [Anna Jaqueline](https://codeberg.org/TheFallenWarrior)