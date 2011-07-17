@echo off
::test
:: playgame.py --player_seed 42 --end_wait=0.25 --verbose --log_dir game_logs --food=random --turns 100 --map_file maps\test_maps\testmap.map %* "java -jar ../dist/GoogleAIAnts.jar" "python sample_bots\python\HunterBot.py"
::game
playgame.py --player_seed 42 --end_wait=0.25 --verbose --log_dir game_logs --turns 1000 --map_file maps\symmetric_maps\symmetric_10.map %* "java -jar ../dist/GoogleAIAnts.jar" "java -jar sample_bots/WSF/17juli.jar" "python sample_bots\python\HunterBot.py" "python sample_bots\python\GreedyBot.py"

::comments

::Options:
::  -h, --help            show this help message and exit
::  -m MAP, --map_file=MAP
::                        Name of the map file
::  -t TURNS, --turns=TURNS
::                        Number of turns in the game
::  --serial              Run bots in serial, instead of parallel.
::  --turntime=TURNTIME   Amount of time to give each bot, in milliseconds
::  --loadtime=LOADTIME   Amount of time to give for load, in milliseconds
::  -r ROUNDS, --rounds=ROUNDS
::                        Number of rounds to play
::  --player_seed=PLAYER_SEED
::                        Player seed for the random number generator
::  --engine_seed=ENGINE_SEED
::                        Engine seed for the random number generator
::  --strict              Strict mode enforces valid moves for bots
::  --capture_errors      Capture errors and stderr in game result
::  --end_wait=END_WAIT   Seconds to wait at end for bots to process end
::  --secure_jail         Use the secure jail for each bot (*nix only)
::  --fill                Fill up extra player starts with last bot specified
::  -p POSITION, --position=POSITION
::                        Player position for first bot specified
::  --attack=ATTACK       Attack method to use for engine. (closest, power,
::                        support, damage)
::  --food=FOOD           Food spawning method. (none, random, sections,
::                        symmetric)
::  --viewradius2=VIEWRADIUS2
::                        Vision radius of ants squared
::  --spawnradius2=SPAWNRADIUS2
::                        Spawn radius of ants squared
::  --attackradius2=ATTACKRADIUS2
::                        Attack radius of ants squared
::  -g GAME_ID, --game=GAME_ID
::                        game id to start at when numbering log files
::  -l LOG_DIR, --log_dir=LOG_DIR
::                        Directory to dump replay files to.
::  -R, --log_replay
::  -S, --log_stream
::  -I, --log_input       Log input streams sent to bots
::  -O, --log_output      Log output streams from bots
::  -E, --log_error       log error streams from bots
::  -e, --log_stderr      additionally log bot errors to stderr
::  -o, --log_stdout      additionally log replay/stream to stdout
::  -v, --verbose         Print out status as game goes.
::  --profile             Run under the python profiler
::  --nolaunch            Prevent visualizer from launching
::  --html=HTML_FILE      Output file name for an html replay