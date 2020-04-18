package stevekung.mods.indicatia.command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.BlockPos;
import stevekung.mods.indicatia.event.IndicatiaEventHandler;

public class CommandApiViewer extends ClientCommandBase
{
    @Override
    public String getCommandName()
    {
        return "sbapi";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length == 0)
        {
            throw new WrongUsageException("/sbapi <player_name>");
        }
        else
        {
            IndicatiaEventHandler.playerToView = args[0];
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        NetHandlerPlayClient connection = Minecraft.getMinecraft().thePlayer.sendQueue;
        List<NetworkPlayerInfo> playerInfo = new ArrayList<>(connection.getPlayerInfoMap());

        if (args.length == 1)
        {
            List<String> playerList = new ArrayList<>();

            for (int i = 0; i < playerInfo.size(); ++i)
            {
                if (i < playerInfo.size())
                {
                    playerList.add(playerInfo.get(i).getGameProfile().getName());
                }
            }
            return CommandBase.getListOfStringsMatchingLastWord(args, playerList);
        }
        return super.addTabCompletionOptions(sender, args, pos);
    }
}