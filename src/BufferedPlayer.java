
// BufferedPlayer.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* Read the sound file as chunks of bytes into a buffer
   via an AudioInputStream, then pass them on to the 
   SourceDataLine. 

   This approach does not require all of the sound file to
   be in memory at the same time.

   I've coded this in a C-style, as a series of static methods
   and static globals. But the approach works inside classes/objects
   as well.

   Changes 16th September
     bug: will not play short WAV files, in similar way to PlayClip.java
     - added checkDuration() to report on length of sound file
 */


import java.io.*;
import javax.sound.sampled.*;


public class BufferedPlayer  
{
  private AudioInputStream stream;
  private AudioFormat format = null;
  private SourceDataLine line = null;


  public BufferedPlayer(String[] args) 
  {
    if (args.length != 1) {
      System.out.println("Usage: java BufferedPlayer <clip file>");
      
    }

    createInput("Sounds/" + args[0]);
    createOutput();

    int numBytes = (int)(stream.getFrameLength() * format.getFrameSize());
         // use getFrameLength() from the stream, since the format 
         // version may return -1 (WAV file formats always return -1)
    System.out.println("Size in bytes: " + numBytes);

    
    play();

    
  } // end of main()

  private void createInput(String fnm)
  // Set up the audio input stream from the sound file
  {
    try {
      // link an audio stream to the sampled sound's file
      stream = AudioSystem.getAudioInputStream( new File(fnm) );
      format = stream.getFormat();
      System.out.println("Audio format: " + format);

      // convert ULAW/ALAW formats to PCM format
      if ( (format.getEncoding() == AudioFormat.Encoding.ULAW) ||
           (format.getEncoding() == AudioFormat.Encoding.ALAW) ) {
        AudioFormat newFormat = 
           new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                                format.getSampleRate(),
                                format.getSampleSizeInBits()*2,
                                format.getChannels(),
                                format.getFrameSize()*2,
                                format.getFrameRate(), true);  // big endian
        // update stream and format details
        stream = AudioSystem.getAudioInputStream(newFormat, stream);
        System.out.println("Converted Audio format: " + newFormat);
        format = newFormat;
      }
    }
    catch (UnsupportedAudioFileException e) 
    {  System.out.println( e.getMessage());  
       
    }
    catch (IOException e) 
    {  System.out.println( e.getMessage());  
       
    }
  }  // end of createInput()


  private void createOutput()
  // set up the SourceDataLine going to the JVM's mixer
  {
    try {
      // gather information for line creation
      DataLine.Info info =
            new DataLine.Info(SourceDataLine.class, format);
      if (!AudioSystem.isLineSupported(info)) {
        System.out.println("Line does not support: " + format);
       
      }
      // get a line of the required format
      line = (SourceDataLine) AudioSystem.getLine(info);
      line.open(format); 
    }
    catch (Exception e)
    {  System.out.println( e.getMessage());  
      
    }
  }  // end of createOutput()


  private void play()
  /* Read  the sound file in chunks of bytes into buffer, and
     pass them on through the SourceDataLine */
  {
    int numRead = 0;
    byte[] buffer = new byte[line.getBufferSize()];

    line.start();
    // read and play chunks of the audio
    try {
      int offset;
      while ((numRead = stream.read(buffer, 0, buffer.length)) >= 0) {
        // System.out.println("read: " + numRead);
        offset = 0;
        while (offset < numRead)
          offset += line.write(buffer, offset, numRead-offset);
      }
      try{Thread.sleep(1);}
      catch(Exception e){
    	  System.out.println(e);
      }
    }
    catch (IOException e) 
    {  System.out.println( e.getMessage()); }

    // wait until all data is played, then close the line
    // System.out.println("drained start");
    line.drain();
    // System.out.println("drained end");
    line.stop();
    line.close();
  }  // end of play()

} // end of BufferedPlayer class
