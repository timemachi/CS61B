package OHRequest;


    public TYIterator(OHRequest queue) {
        super(queue);
    }

    @Override
    public OHRequest next() { // add new functions to superclass
        OHRequest result = super.next();
        if (result != null && result.description.contains("thank u")) {
            result = super.next();
        }
        return result;
    }
}