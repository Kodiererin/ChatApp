package JavaChatProject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
public class ClientHandler implements Runnable
{

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    private Socket socket;		
    private BufferedReader  bufferedReader;		
    private BufferedWriter bufferedWriter; 			
    private String clientUsername;			// username of the client

    public ClientHandler(Socket socket)
    {
        try
        {
            this.socket = socket;			

            new OutputStreamWriter(socket.getOutputStream());

            
//			we will use buffer which will make the communication more efficient.

            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));			// this is what the client is sending

            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));			// this is what we will send.

//			Whenever a client starts then first you have to enter username for the group chat.

            this.clientUsername = bufferedReader.readLine();		
            clientHandlers.add(this);			
            broadCastMessage("Server :"+clientUsername + " has entered the chat");

        } catch(IOException e)
        {
            closeEverything(socket,bufferedReader , bufferedWriter);
        }
    }


    @Override
    public void run()
    {
        // TODO Auto-generated method stub

        String messageFromClient;

        while(socket.isConnected())
        {
            try
            {
                messageFromClient = bufferedReader.readLine();
                broadCastMessage(messageFromClient);

            }catch(IOException e)
            {
                closeEverything(socket,bufferedReader , bufferedWriter);
                break;				// client disconnects then come out of the loop.
            }
        }
    }

    public void broadCastMessage(String messageToSend)
    {
        for(ClientHandler clientHandler : clientHandlers)
        {
            try
            {
                if(!clientHandler.equals(clientUsername))
                {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush(); 			// to send the Data to the server
                }
            }
            catch(IOException e)
            {
                closeEverything(socket,bufferedReader,bufferedWriter);

            }
        }
    }
    public void removeClientHandler()
    {
        clientHandlers.remove(this);
        broadCastMessage("Server"+clientUsername+" has left the Chat");

    }
    public void closeEverything(Socket socket,BufferedReader bufferedReader , BufferedWriter bufferedWriter)
    {
        removeClientHandler();
        try
        {
            if(bufferedReader!=null)
                bufferedReader.close();
            if(bufferedWriter!=null)
                bufferedWriter.close();
            if(socket!=null)
                socket.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
