package io.axoniq.labs.chat.commandmodel;

import io.axoniq.labs.chat.coreapi.*;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;

import java.util.ArrayList;
import java.util.List;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

public class ChatRoom {
    @AggregateIdentifier
    private String roomId;
    private List<String> participants = new ArrayList<>();

    @CommandHandler
    public ChatRoom(CreateRoomCommand cmd) {
        apply(new RoomCreatedEvent(cmd.getRoomId(), cmd.getName()));
    }

    protected ChatRoom() {}

    @CommandHandler
    public void handle(JoinRoomCommand cmd) {
        if (!participants.contains(cmd.getParticipant())) {
            apply(new ParticipantJoinedRoomEvent(cmd.getRoomId(), cmd.getParticipant()));
        }
    }

    @CommandHandler
    public void handle(LeaveRoomCommand cmd) {
        if (participants.contains(cmd.getParticipant())) {
            apply(new ParticipantLeftRoomEvent(cmd.getRoomId(), cmd.getParticipant()));
        }
    }

    @CommandHandler
    public void handle(PostMessageCommand cmd) {
        if (!participants.contains(cmd.getParticipant())) {
            throw new IllegalStateException("Participant can post message only if he/she is in the room");
        }
        apply(new MessagePostedEvent(cmd.getRoomId(), cmd.getParticipant(), cmd.getMessage()));
    }

    @EventSourcingHandler
    public void handle(RoomCreatedEvent event) {
        roomId = event.getRoomId();
    }

    @EventSourcingHandler
    public void handle(ParticipantJoinedRoomEvent event) {
        participants.add(event.getParticipant());
    }

    @EventSourcingHandler
    public void handle(ParticipantLeftRoomEvent event) {
        participants.remove(event.getParticipant());
    }
}