from sqlalchemy import Column, Integer, Float, Text, Boolean
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import composite

Base = declarative_base()

class Monster(Base):
    __tablename__ = 'monsters'

    _id = Column(Integer, primary_key=True)
    name = Column(Text)

class Item(Base):
    __tablename__ = 'items'

    _id = Column(Integer, primary_key=True)
    name = Column(Text)

    rarity = Column(Integer)

    type = Column(Text)