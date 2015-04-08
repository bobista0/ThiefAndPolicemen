package GameEngine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

//dodaæ nr policjanta
//seed
//wyœwietlenie ruchów - spr. iloœæ

public class Main
{
	
	private Shell shell;

    public Main(Display display) {

        shell = new Shell(display);

        shell.setText("Policjanci i Z³odziej");

        initUI(); 
        shell.setSize(860, 590);
        shell.setLocation(300, 300);
        shell.open();

        while (!shell.isDisposed()) {
          if (!display.readAndDispatch()) {
            display.sleep();
          }
        }
    }
    
    public void initUI() {

        FillLayout layout = new FillLayout();
        shell.setLayout(layout);

        new Board(shell);
    }
	
	public static void main(String[] args) throws Exception
	{
		Display display = new Display();
		new Main(display);
		display.dispose();		
	}
}
