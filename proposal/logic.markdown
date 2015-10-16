
Game Objects:

1. Stations
2. Routes/Lines - tunnels embedded
3. Passengers
4. Trains

StationGenerator:

- Check for and generate new stations

PassengerGenerator:

- Check for and generate passenger wave

TrainMonitor:

- Check for train position and boarding station
- Spawn a thread to determine onboard/offboard activity.

Drawer:

- Draw/Update interface
        
DragHandler:

- Verify feasibility of new sections
- Add sections

ClickHandler:

- Misc


Data structure:

- Hyperparams
- Map grid
    + Wet or not
- Station list
    + Coords
    + Type
    + Passenger list
    + Line list (refs)
- Line list
    + Station deque (refs)
    + Locomotive (refs)
- Locomotive list
    + Passenger list (refs)
    + (TODO: fill)
- Passenger list
    + Target: Station type


Backend

Renderer: queries backend each frame



Division of labour:

- davidgao: backend & tuning
- BarclayII: running & debugging & draw stations
- QipengGuo: draw lines & touch handling
- KaiqiangSong: draw stations
- FS: draw lines & touch handling
- MasterChivu: draw stations
