@echo off
playgame.py --engine_seed 42 --food none --end_wait=0.25 --verbose --log_dir game_logs --turns 30 --map_file submission_test/test.map "java -cp ../build/classes/ MyBot" "python submission_test/TestBot.py" -e --nolaunch --strict --capture_errors
# "java -jar GoogleAIAnts.jar ../dist/GoogleAIAnts.jar"