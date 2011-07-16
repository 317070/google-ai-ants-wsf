@echo off
playgame.py --player_seed 42 --end_wait=0.25 --verbose --log_dir game_logs --turns 100 --map_file maps\symmetric_maps\symmetric_10.map %* "java -jar ../dist/GoogleAIAnts.jar" "python sample_bots\python\LeftyBot.py" "python sample_bots\python\HunterBot.py" "python sample_bots\python\LeftyBot.py"

