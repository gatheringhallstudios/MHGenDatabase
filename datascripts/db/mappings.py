from sqlalchemy import Column, Integer, Float, Text, Boolean
from sqlalchemy import ForeignKey, UniqueConstraint
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import composite, relationship

Base = declarative_base()

class Monster(Base):
    __tablename__ = 'monsters'

    _id = Column(Integer, primary_key=True)
    _class = Column('class', Text)
    name = Column(Text)

class Item(Base):
    __tablename__ = 'items'

    _id = Column(Integer, primary_key=True)
    name = Column(Text)

    rarity = Column(Integer)
    type = Column(Text)

    components = relationship("Component", primaryjoin="Item._id == Component.created_item_id")

class Component(Base):
    __tablename__ = 'components'
    
    _id = Column(Integer, primary_key=True)
    created_item_id = Column(Integer, ForeignKey('items._id'))
    component_item_id = Column(Integer, ForeignKey('items._id'))
    key = Column(Boolean)

    created_item = relationship("Item", foreign_keys=[created_item_id])
    component_item = relationship("Item", foreign_keys=[component_item_id])