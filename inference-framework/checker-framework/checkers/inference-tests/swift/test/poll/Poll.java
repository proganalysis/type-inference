package poll;

import webil.ui.*;

public class Poll
{
    final RadioButton choice0;
    final RadioButton choice1;
    final RadioButton choice2;
    final Button voteButton;
    final Button resultButton;
    final Panel mainPanel;

    public static int apple;
    public static int orange;
    public static int grape;

    public Poll()
    {
        choice0 = new RadioButton("Fruit", "Apple");
        choice1 = new RadioButton("Fruit", "Orange");
        choice2 = new RadioButton("Fruit", "Grape");
        voteButton = new Button("Poll");
        resultButton = new Button("Result");

        VerticalPanel vp = new VerticalPanel();
        HorizontalPanel buttonPanel = new HorizontalPanel();

        vp.addChild(choice0);
        vp.addChild(choice1);
        vp.addChild(choice2);
        buttonPanel.addChild(voteButton);
        buttonPanel.addChild(resultButton);

        mainPanel = RootPanel.getRootPanel();
        if (mainPanel != null) {
            mainPanel.addChild(vp);
            mainPanel.addChild(buttonPanel);
        }

        VoteListener voteLi = new VoteListener(this);
        ReportListener rLi = new ReportListener(this);
        voteButton.addListener(voteLi);
        resultButton.addListener(rLi);
    }

    public void vote()
    {
        boolean av = false;
        boolean ov = false;
        boolean gv = false;

        if(choice0!=null) av = choice0.isChecked();
        if(choice1!=null) ov = choice1.isChecked();
        if(choice2!=null) gv = choice2.isChecked();

        boolean appleVoted = av;
        boolean orangeVoted = ov;
        boolean grapeVoted = gv;

        if (appleVoted) Poll.apple = Poll.apple + 1;
        else if (orangeVoted) Poll.orange = Poll.orange + 1;
        else if (grapeVoted) Poll.grape = Poll.grape + 1;
    }

    public void reportResult()
    {
        String winner;
        int max; 
        if (Poll.apple >= Poll.orange) {
            winner = "apple"; max = Poll.apple;
        }
        else {
            winner = "orange"; max = Poll.orange;
        }

        if (max < Poll.grape) {
            winner = "grape"; 
            max = Poll.grape;
        }

        String result = 
            "The winner is " + winner + ".";

        if (mainPanel != null) {
            mainPanel.removeAllChildren();
            mainPanel.addChild(new Text(result));
        }
    }

    public static void main() 
    {
        final Poll app = new Poll();
    }
}

class VoteListener implements ClickListener
{
    final Poll app;

    public VoteListener(Poll v)
    {
        this.app = v;
    }

    public void onClick(Widget w)
    {
        Poll poll = app;
        if(poll != null) {
            poll.vote();
        }
    }
}

class ReportListener implements ClickListener
{
    final Poll app;

    public ReportListener(Poll v) {
        this.app = v;
    }

    public void onClick(Widget w)
    {
        if(app != null) {
            app.reportResult();
        }
    }
}