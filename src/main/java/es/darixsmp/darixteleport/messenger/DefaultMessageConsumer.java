package es.darixsmp.darixteleport.messenger;

import com.google.inject.Inject;
import es.darixsmp.darixteleport.messenger.message.DefaultMessage;
import net.smoothplugins.smoothbase.messenger.MessageConsumer;
import net.smoothplugins.smoothbase.serializer.Serializer;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DefaultMessageConsumer implements MessageConsumer {

    @Inject
    private Serializer serializer;

    @Override
    public void consume(String JSON, @Nullable UUID identifier) {
        DefaultMessage tempMessage = serializer.deserialize(JSON, DefaultMessage.class);
        switch (tempMessage.getType()) {
            // TODO: Implement message handling
        }
    }
}
