# FFOnline

FFOnline is a Java reimplementation of the original NES release of Final Fantasy 1, inspired by Multi-User Dungeons (MUDs). It is a learning project for me to explore online game server programming.

FFOnline is being designed to be reasonably true to FF1, but as a text-based game. Because of that, it plays quite different from a regular MUD, since each player controls a 4-character party and roleplaying and player-to-player interaction are very limited.

The game is still in development and as of now, it's not yet functional.

## Todo
 - ~~Simple TCP server that accepts connections~~
 - ~~Global chat~~
 - ~~Items and equipment~~
 - Player character and progression
   - ~~Equipment and stat calculation~~
   - ~~Creating new characters from JSON job data~~
   - Learning magic spells
   - Leveling up, EXP and stat curves
 - ~~Magic and enemies' special attacks~~
 - Player party and player-facing character creation
 - ~~Game state manager with command dispatching~~
 - Encode enemy data and AI
 - Battle commands
   - ~~Base Command class~~
   - ~~FIGHT command~~
   - Magic commands (MAGIC, DRINK, ITEM)
   - ~~RUN command~~
 - Enemy formations
 - Battle engine with turn/command orchestration
 - Using magic and items out of battle
 - Locations (overworld, towns, shops, dungeons)
 - Transportation (SHIP, AIRSHIP, CANOE)
 - Local chat and whispers
 - Key items and game progress

## Limitations

 - Networking is plain text only; support for encryption, compression, or MUD-specific protocols (e.g. GMCP, MSSP, etc.) is not planned
 - No persistent data is stored, your session and game progress are lost as soon as you disconnect
 - Random numbers are generated using `java.util.Random`, as FFOnline does not implement the original RNG algorithm. If you are well-acquainted to FF1's behavior, you might notice some deviation

## How to Build

FFOnline is a Netbeans Ant project.

### Requirements

 - JDK 25
 - Apache Ant

 ### Build from command line

```bash
ant clean jar
```

This creates `dist/FFOnline.jar`.

### Run from command line

```bash
ant run
# or
java -jar dist/FFOnline.jar
```

### Build/run from NetBeans IDE

1. Open the project in NetBeans IDE.
2. Use **Build Project** or **Run Project**.

## Credits
Code by [Anna Jaqueline](https://codeberg.org/TheFallenWarrior)

FFOnline wouldn't be possible without the following documentation:
 - FFBytes by Y Dienyddiwr Da
 - [Final Fantasy - Game Mechanics Guide](http://www.gamefaqs.com/nes/522595-final-fantasy/faqs/57009) by AstralEsper
 - [Final Fantasy 1 Disassembly](https://github.com/Entroper/FF1Disassembly) by Disch