# FFOnline

FFOnline is a Java reimplementation of the original NES release of Final Fantasy 1, inspired by Multi-User Dungeons (MUDs). It is a learning project for me to explore online game server programming.

FFOnline is being designed to be reasonably true to FF1, within the constraints of the conversion to a text-based game. Because of that, it plays quite different from a regular MUD, since each player controls a 4-character party and player-to-player interaction is very limited.

The game is still in development and as of now, it's not yet functional.

## Todo
 - ~~Simple TCP server that accepts connections~~
 - ~~Global chat~~
 - ~~Items and equipment~~
 - Player character and progression
   - ~~Equipment and stat calculation~~
   - Creating new characters from JSON job data
   - Leveling up, EXP and stat curves
 - Magic and enemies' special attacks
 - Player party and player-facing character creation
 - Encode enemy data and AI
 - Battle commands (FIGHT, MAGIC, DRINK, etc.)
 - Enemy formations
 - Battle system
 - Using magic and items out of battle
 - Locations (overworld, towns, shops, dungeons)
 - Transportation (SHIP, AIRSHIP, CANOE)
 - Local chat and whispers
 - Key items and game progress

## Limitations

 - Networking is plain text only; support for encryption, compression, or MUD-specific protocols (e.g. GMCP, MSSP, etc.) is not planned
 - No persistent data is stored, your account and game progress are lost as soon as you disconnect; data storage in a database is not a planned feature
 - Random numbers are generated using `java.util.Random`, as FFOnline does not reimplement the original RNG algorithm. If you are well-acquainted to FF1's behavior, you might notice some deviation.

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

### Build/run from NetBeans

1. Open the project in NetBeans.
2. Use **Build Project** or **Run Project**.

## Credits
Code by [Anna Jaqueline](https://codeberg.org/TheFallenWarrior)

Based on the [Final Fantasy - Game Mechanics Guide](http://www.gamefaqs.com/nes/522595-final-fantasy/faqs/57009) by AstralEsper