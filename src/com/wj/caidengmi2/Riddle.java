package com.wj.caidengmi2;

public class Riddle
{
  private int answer;
  private String[] answers;
  private String riddle;

  public int getAnswer()
  {
    return this.answer;
  }

  public String[] getAnswers()
  {
    return this.answers;
  }

  public String getRiddle()
  {
    return this.riddle;
  }

  public void setAnswer(int paramInt)
  {
    this.answer = paramInt;
  }

  public void setAnswers(String[] paramArrayOfString)
  {
    this.answers = paramArrayOfString;
  }

  public void setRiddle(String paramString)
  {
    this.riddle = paramString;
  }
}
