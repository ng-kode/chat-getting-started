package io.axoniq.labs.chat.query.rooms.summary;

import io.axoniq.labs.chat.coreapi.AllRoomsQuery;
import io.axoniq.labs.chat.coreapi.ParticipantJoinedRoomEvent;
import io.axoniq.labs.chat.coreapi.ParticipantLeftRoomEvent;
import io.axoniq.labs.chat.coreapi.RoomCreatedEvent;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoomSummaryProjection {

    private final RoomSummaryRepository roomSummaryRepository;

    public RoomSummaryProjection(RoomSummaryRepository roomSummaryRepository) {
        this.roomSummaryRepository = roomSummaryRepository;
    }

    @EventHandler
    public void handle(RoomCreatedEvent event) {
        roomSummaryRepository.save(new RoomSummary(
           event.getRoomId(),
           event.getName()
        ));
    }

    @EventHandler
    public void handle(ParticipantJoinedRoomEvent event) {
        roomSummaryRepository.getOne(event.getRoomId()).addParticipant();
    }

    @EventHandler
    public void handle(ParticipantLeftRoomEvent event) {
        roomSummaryRepository.getOne(event.getRoomId()).removeParticipant();
    }

    @QueryHandler
    public List<RoomSummary> handle(AllRoomsQuery query) {
        return roomSummaryRepository.findAll();
    }
}
